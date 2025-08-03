
import pymongo #bibliothèque pour interagir avec MongoDB
import paho.mqtt.client as mqtt  #bibliothèque pour se connecter à un broker MQTT
import time #permet d’attendre/suspendre le programme
from kafka import KafkaProducer
import json #pour la serialisation des données
from kafka.errors import KafkaError
from datetime import datetime, timedelta  #pour la gestion du temps
from bson.objectid import ObjectId #pour gérer les identifiants uniques des documents
import crcmod
from datetime import datetime, timezone, timedelta
import threading
from Crypto.Cipher import AES
import base64
# Configuration
BROKER = "broker.hivemq.com" #l'adresse du serveur MQTT public 
PORT = 1883  #port MQTT par defaut
TOPIC_PUBLISH = "data1"  # Pour l'envoi de commandes à la passerelle
TOPIC_SUBSCRIBE = "data"  # Pour écouter les messages entrants du passerelle
KAFKA_TOPIC = "data3"  # Pour publier les reponses de la passerelle au client spring boot
KAFKA_BROKER = "localhost:9092" # Adresse de mon serveur Kafka
MAX_RETRIES = 3
RETRY_DELAY = 15 # en secondes
AES_KEY = b'1234567890abcdef'
# Initialisation du producteur Kafka
kafka_producer = KafkaProducer(   #on cree une instance de KafkaProducer
    bootstrap_servers=KAFKA_BROKER,  #bootstrap_servers parameter defines the Kafka broker address
    value_serializer=lambda v: json.dumps(v).encode("utf-8")   ## Create a producer with JSON serializer
)

# Initialisation du client MongoDB
mongo_client = pymongo.MongoClient("mongodb://localhost:27017/") # Connexion à MongoDB
db = mongo_client["query_db"]  #Sélectionne la base query_db
waitCommand = db["Wait_Command"]
sensorConfig = db["Sensor_Config"]
actConfig = db["Actuator_Config"]
doAction = db["DoAction_Config"]
deviceConfig = db["Device_Config"]
getData = db["GetData_Config"]
logs_collection = db["Logs"]

# Dictionnaire pour suivre les commandes en attente
pending_command = None

def send_to_kafka(payload, attempt=1):
    future = kafka_producer.send(KAFKA_TOPIC, payload)  #envoie du payload au client springboot

    def on_success(record_metadata):
        print(f"[KAFKA] Message envoyé avec succès à {record_metadata.topic}")
        print(f" le contenu est {payload}")

    def on_error(excp):
        print(f"[KAFKA] Échec de l'envoi (tentative {attempt}) : {excp}")
        if attempt < MAX_RETRIES:
            print(f"[KAFKA] Réessai dans {RETRY_DELAY} secondes...")
            time.sleep(RETRY_DELAY)  #on attend 2 second avant le renvoie du mesg
            send_to_kafka(payload, attempt + 1)
        else:
            print("[KAFKA] Échec permanent : message abandonné.")

    future.add_callback(on_success)
    future.add_errback(on_error)

# CRC STM32-compatible : poly 0x04C11DB7, init 0xFFFFFFFF, no reverse, no final XOR
crc32_stm32 = crcmod.mkCrcFun(
    poly=0x104C11DB7,     # CRC-32 polynomial + implicit leading 1
    initCrc=0xFFFFFFFF,
    rev=False,
    xorOut=0x00000000     # STM32 doesn't apply final XOR
)

def calculate_crc32_stm32(data: bytes) -> int:
    return crc32_stm32(data)

def traiter_trame_3E(client, parts):
    if len(parts) < 5:
        print("[ERREUR] Trame 3E invalide ")
        return

    identifiant = parts[1]
    try:
        received_timestamp = int(parts[3])
    except ValueError:
        print("[ERREUR] Timestamp invalide : ", parts[3])
        return
    utc = int(parts[2])

    # Obtenir le timestamp actuel pour la Tunisie (UTC+1 sans changement d'heure)
    tunisia_time = datetime.now(timezone(timedelta(hours=utc))) #horaire de la Tunisie par rapport à UTC
    current_timestamp_tunisia = int(tunisia_time.timestamp()) + 3600*utc
    print(f"DEBUG: Timestamp actuel service (Tunisie UTC+1) : {current_timestamp_tunisia} ({tunisia_time})")

    # Comparer la différence (par ex. 10 secondes de tolérance)
     # Étape 1: Vérifier si un décalage de temps est détecté
    if abs(current_timestamp_tunisia - received_timestamp) > 60:
        print(f"[SYNC] Décalage de temps détecté pour {identifiant}. Différence: {current_timestamp_tunisia - received_timestamp} secondes.")
        
        frame = f"3E;{identifiant};{current_timestamp_tunisia};"
            # time.sleep(1) peut rester si nécessaire pour la stabilité de la communication LoRa
        publish_frame(client, frame)
    else:
        print("[SYNC] Timestamp correct, aucune correction nécessaire.")    



def pad(text):
    # Pad to 16 bytes (PKCS7 padding)
    pad_len = 16 - len(text) % 16
    return text + chr(pad_len) * pad_len

def encrypt_and_encode(plaintext):
    cipher = AES.new(AES_KEY, AES.MODE_ECB)
    padded_text = pad(plaintext)
    encrypted_bytes = cipher.encrypt(padded_text.encode('utf-8'))
    return base64.b64encode(encrypted_bytes).decode('utf-8')
# Fonction appelée quand un message est reçu sur le topic MQTT "data"
def unpad(text):
    pad_len = ord(text[-1])
    return text[:-pad_len]

def decode_and_decrypt(encoded_text):
    cipher = AES.new(AES_KEY, AES.MODE_ECB)
    encrypted_bytes = base64.b64decode(encoded_text)
    decrypted_padded = cipher.decrypt(encrypted_bytes).decode('utf-8')
    return unpad(decrypted_padded)
# Fonction appelée quand un message est reçu sur le topic MQTT "data"
def on_message(client, userdata, msg):
    
    global pending_command
    payload = msg.payload.decode('utf-8')

    try:
        c_payload = msg.payload.decode('utf-8')
    except UnicodeDecodeError:
        print("⚠️ Message reçu non décodable en UTF-8 :", msg.payload)
        return

    print(f"📩 Reçu sur MQTT ({msg.topic}) : {payload}")
    try:
         payload = decode_and_decrypt(c_payload)
    except Exception as e:
        print(f"[ERREUR] Déchiffrement échoué : {e}")
        return

    print(f"🔓 Déchiffré : {payload}")

    # Étape 1 : Extraire et vérifier le CRC
    # Nettoyage des parties vides en fin de chaîne
    parts = [p for p in payload.split(";") if p != ""]
    
    if len(parts) < 2:
        print("[ERREUR] Message trop court pour contenir des données + CRC.")
        return

    received_crc_str = parts[-1].strip()  # dernier champ : CRC attendu
    try:
        received_crc = int(received_crc_str, 16)  # supposé hexadécimal
    except ValueError:
        print(f"[ERREUR] CRC reçu invalide : '{received_crc_str}'")
        return

    data_without_crc = ";".join(parts[:-1])+ ";"  # chaîne à vérifier
    data_bytes = data_without_crc.encode('utf-8')
    calculated_crc = calculate_crc32_stm32(data_bytes)

    if calculated_crc != received_crc:
        print(f"[ERREUR] CRC invalide. Calculé: {calculated_crc:08X}, Reçu: {received_crc_str}")
        return  # Ignore le message

    print(f"[OK] CRC valide : {calculated_crc:08X}")
    # ⚠️ Utiliser la chaîne sans CRC pour le reste
    payload = data_without_crc
    parts = payload.split(";")
    codDevice = parts[1]
    cmd_type = parts[0]
  
    if parts[0] == "3E":
        traiter_trame_3E(client, parts)
        return

    if pending_command and codDevice == pending_command["codDevice"]:
     try:
        if cmd_type == "RACK":
            print(f"ACK request received from {codDevice}, sending ACK")
            waitCommand.update_one({"_id": pending_command["wait_command_id"]},
                                   {"$set": {"ack_requested": True}})
            ack_frame = f"ACK;{codDevice};"
            publish_frame(client, ack_frame)
            return

        elif cmd_type == "CFs":
            print(f"Command confirmed by device {codDevice}")
            waitCommand.update_one({"_id": pending_command["wait_command_id"]},
                                   {"$set": {"confirmed": True, "status": "confirmed"}})
            
            confirmation_str = f"{pending_command['codDevice']};{pending_command['output']}:{pending_command.get('etat', 'unknown')}:{pending_command.get('index', '0')};{datetime.now().timestamp()}"
            send_to_kafka(confirmation_str)    
            pending_command = None
           
            return

        else:
            waitCommand.update_one({"_id": pending_command["wait_command_id"]},
                                   {"$set": {"response": payload}})
            logs_collection.update_one({"_id": ObjectId(pending_command["log_id"])},
                                       {"$set": {"response": payload}})
     except Exception as e:
        print(f"[ERROR] Error while handling response for {codDevice}: {e}")
    else:
     print("🔍 Réponse reçue mais aucune commande en attente")
   

   

    

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("✅ Connecté au broker MQTT")
        client.subscribe(TOPIC_SUBSCRIBE)  # <-- Réabonnement ici
    else:
        print(f"❌ Échec de connexion, code de retour : {rc}")


def on_disconnect(client, userdata, rc):  #on traite le cas où il y'a une disconnexion du broker MQTT
    print("🔌 Déconnecté du broker MQTT.")
    while True:
        try:
            print("Tentative de reconnexion au broker MQTT...")
            client.reconnect()
            print("Reconnecté avec succès !")
            break
        except Exception as e:
            print(f"Reconnexion échouée : {e}")
            time.sleep(5)

def connect_mqtt():  #coonexion au broker
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect  # Ajoute le callback
    client.on_message = on_message #automatiquement appelée chaque fois qu’un message arrive sur un topic abonné 
    client.connect(BROKER, PORT, 60)
    client.subscribe(TOPIC_SUBSCRIBE)
    client.loop_start()  #execute sur une thread 
    print("Connecté au broker MQTT")
    return client

def publish_frame(client, frame):
    # Supprimer les espaces ou sauts de ligne involontaires
    frame = frame.strip()
   
    # Calcul du CRC (sans le CRC lui-même)
    data_bytes = frame.encode('utf-8') #pour convertir une chaîne de caractères (str) en une suite d'octets 
    calculated_crc = calculate_crc32_stm32(data_bytes)

    # Ajouter le CRC sous forme hexadécimale (ex: '1A2B3C4D') et terminer par ;
    crc_str = f"{calculated_crc:08X};"
    full_frame = frame + crc_str
    print(f"📤 Payload à envoyer (avant chiffrement) : {full_frame}")
    encrypted_payload = encrypt_and_encode(full_frame)
    result = client.publish(TOPIC_PUBLISH, encrypted_payload) #Utilise le client MQTT pour publier une Trame (chaîne de caractères) sur le TOPIC
    #result.wait_for_publish() # Attendre que la publication soit terminée
    if result.rc == mqtt.MQTT_ERR_SUCCESS:
        print(f"[MQTT] Frame effectivement publiée : {encrypted_payload}")
    else:
        print(f"[ERREUR MQTT] Code retour : {result.rc}")
    time.sleep(1)

def process_next_command(client):
    global pending_command

    if pending_command:
        return  # Attendre que la commande en cours soit traitée
    doc = waitCommand.find_one(
    {"status": "pending"}  # descending to get latest document first
)
    if not doc:
     return




    #on extrait les informqtion nécessaire du doccument pour la recherche
    codDevice = doc.get("codDevice") 
    command = doc.get("command")
    idU = doc.get("idU")
    wait_command_id = doc["_id"]
   
    print(f"Traitement : codDevice={codDevice}, command={command}, idU={idU}")

    # Sélection de la collection cible
    target_collection = None
    if command == "CFGA":
        target_collection = actConfig
    elif command == "DOA":
        target_collection = doAction
    elif command == "CFGD":
        target_collection = deviceConfig
    elif command == "GETD":
        target_collection = getData
    elif command == "CFGS":
        target_collection = sensorConfig
    else:
        print(f"Commande inconnue : {command}")
        waitCommand.delete_one({"_id": wait_command_id})
        return

    # Construction de la requête MongoDB
    query = {"codDevice": codDevice}
    if idU is not None:
        query["idU"] = idU

    result = target_collection.find_one(query ,  sort=[("_id", -1)])  #on recupere le premier doc dans la collection cible
    if not result or not result.get("frame"):
        print("⚠️ Aucun frame trouvé.")
        waitCommand.delete_one({"_id": wait_command_id})
        return

    frame = result["frame"] #on recupere la trame de commande à envoyer
    etat = None
    index = None 
    output = None
    frame_parts = frame.split(";")
    if len(frame_parts) > 3:
        etat = frame_parts[3]
        index = frame_parts[4]
        output = frame_parts[2]  
    else:
        etat = "unknown"  # or None or ""

    
  
    # Créer un document Logs
    log_doc = {
        "codDevice": codDevice,
        "date": datetime.now(),
        "request": frame,
        "response": None
    }
    inserted_log = logs_collection.insert_one(log_doc)  #on insere les données dans la collection Logs
    log_id = inserted_log.inserted_id  #on enregistre id
    print(f"[LOG] Document inséré avec ID : {log_id}")

    # Publier la trame
    publish_frame(client, frame)

    # Enregistrer la commande en attente
    pending_command = {
        "codDevice": codDevice,
        "command": command,
        "frame": frame,
        "log_id": log_id,
        "wait_command_id": wait_command_id,
        "target_id": result["_id"] if command in ("DOA", "GETD") else None,  #on enregistre id si la commande est DOA ou GETD
        "sent_time": datetime.now(),
        "retry_count": 0,
        "idU": idU ,
        "etat": etat, 
         "index": index,
         "output" : output 
    }

def check_pending_command(client):
    global pending_command
    if not pending_command:
        return

    

    elapsed_time = (datetime.now() - pending_command["sent_time"]).total_seconds()
   

    if elapsed_time > RETRY_DELAY:
        if pending_command.get("confirmed", False):
            print(f"[OK] Command {pending_command['command']} was confirmed.")
            return

        if pending_command["retry_count"] < MAX_RETRIES:
            # Resend frame
            frame = pending_command["frame"]
            publish_frame(client, frame)
            pending_command["retry_count"] += 1
            pending_command["sent_time"] = datetime.now()
           
            print(f"[RETRY] Resent frame to {pending_command['codDevice']} (Retry {pending_command['retry_count']}/{MAX_RETRIES})")
            
            logs_collection.update_one(
                {"_id": ObjectId(pending_command["log_id"])},
                {"$set": {"response": f"Tentative de renvoi {pending_command['retry_count']} / {MAX_RETRIES}"}}
            )
        else:
            # Mark as failed
            print(f"[FAIL] Command to {pending_command['codDevice']} failed after {MAX_RETRIES} retries.")
            waitCommand.update_one(
                {"_id": pending_command["wait_command_id"]},
                {"$set": {"status": "failed", "ack_requested": False, "confirmed": False}}
            )
            logs_collection.update_one(
                {"_id": ObjectId(pending_command["log_id"])},
                {"$set": {"response": "Échec : aucune confirmation après plusieurs tentatives."}}
            )
            pending_command = None


def main():
    mqtt_client = connect_mqtt()
    
    try:
        while True:
            process_next_command(mqtt_client)
            check_pending_command(mqtt_client)
            time.sleep(1)
    except KeyboardInterrupt:  #Pour quand je fais Ctrl+C dans le terminal, je recois le mesg du print 
        print("Arrêt du programme.")  #au lieu d'un mesg d'erreur
    finally:  #exécuté quand on sort définitivement du try
        mqtt_client.loop_stop()
        mqtt_client.disconnect()
        mongo_client.close()

if __name__ == "__main__":
    main()


