/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file    subghz_phy_app.c
  * @author  MCD Application Team
  * @brief   Application of the SubGHz_Phy Middleware
  ******************************************************************************
  * @attention
  *
  * Copyright (c) 2021 STMicroelectronics.
  * All rights reserved.
  *
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  *
  ******************************************************************************
  */
/* USER CODE END Header */

/* Includes ------------------------------------------------------------------*/
#include "platform.h"
#include "sys_app.h"
#include "subghz_phy_app.h"
#include "radio.h"
#include "aes.h"
#include "base64.h"

/* USER CODE BEGIN Includes */
#include "stm32_timer.h"
#include "stm32_seq.h"
#include "adc_if.h"
#include "utilities_def.h"
#include "app_version.h"
#include "subghz_phy_version.h"
#include "stm32_systime.h"
#include "crc.h"
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include <stdlib.h>
/* USER CODE END Includes */

/* External variables ---------------------------------------------------------*/
/* USER CODE BEGIN EV */

/* USER CODE END EV */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
/* Configurations */
/*Timeout*/
#define RX_TIMEOUT_VALUE              15000
#define TX_TIMEOUT_VALUE              5000
/* PING string*/
#define PING "PING"
/* PONG string*/
#define PONG "PONG"
/* error string*/
#define Error "Erreur"
/*Size of the payload to be sent*/
/* Size must be greater of equal the PING and PONG*/
#define MAX_APP_BUFFER_SIZE          255  //size max du payload
#if (PAYLOAD_LEN > MAX_APP_BUFFER_SIZE)
#error PAYLOAD_LEN must be less or equal than MAX_APP_BUFFER_SIZE
#endif /* (PAYLOAD_LEN > MAX_APP_BUFFER_SIZE) */
/* wait for remote to be in Rx, before sending a Tx frame*/
#define RX_TIME_MARGIN                200  // temps du passage du rx à tx
/* Afc bandwidth in Hz */
#define FSK_AFC_BANDWIDTH             83333
/* LED blink Period*/
#define LED_PERIOD_MS                 200

/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
/* Radio events function pointer */
static RadioEvents_t RadioEvents;

/* USER CODE BEGIN PV */
/*Ping Pong FSM states */
States_t State = TX;
/* App Rx Buffer*/
static uint8_t BufferRx[MAX_APP_BUFFER_SIZE];
/* App Tx Buffer*/
static uint8_t BufferTx[MAX_APP_BUFFER_SIZE];
/* Last  Received Buffer Size*/
uint16_t RxBufferSize = 0;
/* Last  Received packer Rssi*/
int8_t RssiValue = 0;
/* Last  Received packer SNR (in Lora modulation)*/
int8_t SnrValue = 0;
// temperature value
int16_t temperature2 = 0;
// previous temperature value
int16_t prv_temperature = 0;
// battery  level
uint8_t battery2 = 0;
uint8_t cmd_3F = 0;
uint8_t cmd_3C = 0;
uint8_t repCmd = 0;
uint8_t utc = 1;   //configuration du time pour la tunisie
// previous battery  level
int8_t prv_battery = 0;
int8_t SensorSendData = 0;  //bool pour verifier si le temps d'envoie de données est arriver
uint8_t ActuatorSendData = 0;    //bool pour verifier si le temps d'envoie de données est arriver
/* Led Timers objects*/
static UTIL_TIMER_Object_t timerLed;
static UTIL_TIMER_Object_t SensorTimer;
static UTIL_TIMER_Object_t ActuatorTimer;
extern CRC_HandleTypeDef hcrc;
char tempReceivedFrame[MAX_APP_BUFFER_SIZE] = {0};
bool waitingForAck = false;
bool ackReceived = false;
/* random delay to make sure 2 devices will sync*/
/* the closest the random delays are, the longer it will
   take for the devices to sync when started simultaneously*/
static int32_t random_delay;

///---------------energy variables---------------////
#define TX_CURRENT_A 0.042f   // 42 mA for TX @ 14 dBm
#define RX_CURRENT_A 0.0055f  // 5.5 mA for RX LoRa 125 kHz SMPS mode

static float totalTxEnergy_J = 0.0f;
static float totalRxEnergy_J = 0.0f;

static uint32_t txStartTime = 0;
static uint32_t rxStartTime = 0;

static uint16_t batteryBeforeTx = 0;
static uint16_t batteryBeforeRx = 0;
bool txEnergyReady = false;
bool rxEnergyReady = false;
char pendingEnergyFrame[64];  // or larger if needed
bool sendRxEnergyNext = false;


char midtxEnergyBuffer[MAX_APP_BUFFER_SIZE];
char midrxEnergyBuffer[MAX_APP_BUFFER_SIZE];
char txEnergyBuffer[MAX_APP_BUFFER_SIZE];
char rxEnergyBuffer[MAX_APP_BUFFER_SIZE];

static const uint8_t aes_key[16] = "1234567890abcdef";

/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
/*!
 * @brief Function to be executed on Radio Tx Done event
 */
static void OnTxDone(void);

/**
  * @brief Function to be executed on Radio Rx Done event
  * @param  payload ptr of buffer received
  * @param  size buffer size
  * @param  rssi
  * @param  LoraSnr_FskCfo
  */
static void OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t LoraSnr_FskCfo);

/**
  * @brief Function executed on Radio Tx Timeout event
  */
static void OnTxTimeout(void);

/**
  * @brief Function executed on Radio Rx Timeout event
  */
static void OnRxTimeout(void);

/**
  * @brief Function executed on Radio Rx Error event
  */
static void OnRxError(void);

/* USER CODE BEGIN PFP */
/**
  * @brief  Function executed on when led timer elapses
  * @param  context ptr of LED context
  */
static void OnledEvent(void *context);
static void triggerWaitTx (void *context);
void buildDataString(char* buffer, Device app, uint8_t battery, Capteur* sensors[], int nbSensors);
void buildActuatorDataString(char *buffer, const char *codDevice, uint8_t battery);
static void triggerWaitActuator(void *context);
void buildDeviceDataString(char *buffer, const char *codDevice, uint8_t battery, Capteur **sensors, int nbSensors,
                           Actionneur **outputs, int nbOutputs);
Capteur* find_sensor_by_index_and_type(int index, const char* type);
Actionneur* find_actionneur_by_index_and_output(int index, const char* output);
void traiter_commande_recue(const char* message);
void appendTimeToBuffer(char* buffer, uint32_t timestamp);
void AppendCRCToBuffer(CRC_HandleTypeDef *hcrc);
/**
  * @brief PingPong state machine implementation
  */
static void PingPong_Process(void);

/* USER CODE END PFP */

/* Exported functions ---------------------------------------------------------*/
void SubghzApp_Init(void)
{
  /* USER CODE BEGIN SubghzApp_Init_1 */

  APP_LOG(TS_OFF, VLEVEL_M, "\n\rPING PONG\n\r");
  /* Get SubGHY_Phy APP version*/
  APP_LOG(TS_OFF, VLEVEL_M, "APPLICATION_VERSION: V%X.%X.%X\r\n",
          (uint8_t)(APP_VERSION_MAIN),
          (uint8_t)(APP_VERSION_SUB1),
          (uint8_t)(APP_VERSION_SUB2));

  /* Get MW SubGhz_Phy info */
  APP_LOG(TS_OFF, VLEVEL_M, "MW_RADIO_VERSION:    V%X.%X.%X\r\n",
          (uint8_t)(SUBGHZ_PHY_VERSION_MAIN),
          (uint8_t)(SUBGHZ_PHY_VERSION_SUB1),
          (uint8_t)(SUBGHZ_PHY_VERSION_SUB2));

  /* Led Timers*/
  //UTIL_TIMER_Create(&timerLed, LED_PERIOD_MS, UTIL_TIMER_ONESHOT, OnledEvent, NULL);
  //UTIL_TIMER_Start(&timerLed);
  UTIL_TIMER_Create(&SensorTimer, app.PS*60000, UTIL_TIMER_ONESHOT, triggerWaitTx, NULL);
  UTIL_TIMER_Start(&SensorTimer); // Démarrer le timer immédiatement
  if (app.PA != app.PS) {
      // Timer actionneur séparé
      UTIL_TIMER_Create(&ActuatorTimer, app.PA*60000 , UTIL_TIMER_ONESHOT, triggerWaitActuator, NULL);
      UTIL_TIMER_Start(&ActuatorTimer);
  }
  /* USER CODE END SubghzApp_Init_1 */

  /* Radio initialization */
  RadioEvents.TxDone = OnTxDone;
  RadioEvents.RxDone = OnRxDone;
  RadioEvents.TxTimeout = OnTxTimeout;
  RadioEvents.RxTimeout = OnRxTimeout;
  RadioEvents.RxError = OnRxError;

  Radio.Init(&RadioEvents);

  /* USER CODE BEGIN SubghzApp_Init_2 */
  /*calculate random delay for synchronization*/
  random_delay = (Radio.Random()) >> 22; /*10bits random e.g. from 0 to 1023 ms*/

  /* Radio Set frequency */
  Radio.SetChannel(RF_FREQUENCY);

  /* Radio configuration */
#if ((USE_MODEM_LORA == 1) && (USE_MODEM_FSK == 0))
  APP_LOG(TS_OFF, VLEVEL_M, "---------------\n\r");
  APP_LOG(TS_OFF, VLEVEL_M, "LORA_MODULATION\n\r");
  APP_LOG(TS_OFF, VLEVEL_M, "LORA_BW=%d kHz\n\r", (1 << LORA_BANDWIDTH) * 125);
  APP_LOG(TS_OFF, VLEVEL_M, "LORA_SF=%d\n\r", LORA_SPREADING_FACTOR);

  Radio.SetTxConfig(MODEM_LORA, TX_OUTPUT_POWER, 0, LORA_BANDWIDTH,
                    LORA_SPREADING_FACTOR, LORA_CODINGRATE,
                    LORA_PREAMBLE_LENGTH, LORA_FIX_LENGTH_PAYLOAD_ON,
                    true, 0, 0, LORA_IQ_INVERSION_ON, TX_TIMEOUT_VALUE);

  Radio.SetRxConfig(MODEM_LORA, LORA_BANDWIDTH, LORA_SPREADING_FACTOR,
                    LORA_CODINGRATE, 0, LORA_PREAMBLE_LENGTH,
                    LORA_SYMBOL_TIMEOUT, LORA_FIX_LENGTH_PAYLOAD_ON,
                    0, true, 0, 0, LORA_IQ_INVERSION_ON, true);

  Radio.SetMaxPayloadLength(MODEM_LORA, MAX_APP_BUFFER_SIZE);

#elif ((USE_MODEM_LORA == 0) && (USE_MODEM_FSK == 1))
  APP_LOG(TS_OFF, VLEVEL_M, "---------------\n\r");
  APP_LOG(TS_OFF, VLEVEL_M, "FSK_MODULATION\n\r");
  APP_LOG(TS_OFF, VLEVEL_M, "FSK_BW=%d Hz\n\r", FSK_BANDWIDTH);
  APP_LOG(TS_OFF, VLEVEL_M, "FSK_DR=%d bits/s\n\r", FSK_DATARATE);

  Radio.SetTxConfig(MODEM_FSK, TX_OUTPUT_POWER, FSK_FDEV, 0,
                    FSK_DATARATE, 0,
                    FSK_PREAMBLE_LENGTH, FSK_FIX_LENGTH_PAYLOAD_ON,
                    true, 0, 0, 0, TX_TIMEOUT_VALUE);

  Radio.SetRxConfig(MODEM_FSK, FSK_BANDWIDTH, FSK_DATARATE,
                    0, FSK_AFC_BANDWIDTH, FSK_PREAMBLE_LENGTH,
                    0, FSK_FIX_LENGTH_PAYLOAD_ON, 0, true,
                    0, 0, false, true);

  Radio.SetMaxPayloadLength(MODEM_FSK, MAX_APP_BUFFER_SIZE);

#else
#error "Please define a modulation in the subghz_phy_app.h file."
#endif /* USE_MODEM_LORA | USE_MODEM_FSK */

  /*fills tx buffer*/
  memset(BufferTx, 0x0, MAX_APP_BUFFER_SIZE); //used to fill a block of memory with a particular value

  APP_LOG(TS_ON, VLEVEL_L, "rand=%d\n\r", random_delay);
  /*starts reception*/
  Radio.Rx(RX_TIMEOUT_VALUE + random_delay); //pour éviter les interférences et permettre à la radio de se réveiller.

  /*register task to to be run in while(1) after Radio IT*/
  UTIL_SEQ_RegTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), UTIL_SEQ_RFU, PingPong_Process);
  //RFU = Reserved for Future Use défini à 0, ce qui signifie qu'il n'est pas utilisé pour le moment.
  UTIL_SEQ_RegTask((1 << CFG_SEQ_Task_Control_Actuators), UTIL_SEQ_RFU, control_actuators);
  /* USER CODE END SubghzApp_Init_2 */
}

/* USER CODE BEGIN EF */
void SysTimeLocalTime(const uint32_t timestamp, struct tm *localtime);
/* USER CODE END EF */

/* Private functions ---------------------------------------------------------*/
static void OnTxDone(void)
{
  /* USER CODE BEGIN OnTxDone */
  APP_LOG(TS_ON, VLEVEL_L, "OnTxDone\n\r");  //enregistre un message dans les logs pour indiquer que la transmission est terminée.
  if (sendRxEnergyNext) {
      sendRxEnergyNext = false;
      Radio.Send((uint8_t*)pendingEnergyFrame, strlen((char*)pendingEnergyFrame));
  }

  /* Update the State of the FSM*/
  State = TX;
  /* Run PingPong process in background*/
  UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
  /* USER CODE END OnTxDone */
}

static void OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t LoraSnr_FskCfo)
{
  /* USER CODE BEGIN OnRxDone */
  APP_LOG(TS_ON, VLEVEL_L, "OnRxDone\n\r");  ///enregistre un message dans les logs pour indiquer que la réception est terminée.
#if ((USE_MODEM_LORA == 1) && (USE_MODEM_FSK == 0))
  APP_LOG(TS_ON, VLEVEL_L, "RssiValue=%d dBm, SnrValue=%ddB\n\r", rssi, LoraSnr_FskCfo);
    /* Run PingPong process in background*/
       /* Clear BufferRx*/
      State = RX;
      /* Clear BufferRx*/
      memset(BufferRx, 0, MAX_APP_BUFFER_SIZE);
      /* Record payload size*/
      RxBufferSize = size;
      if (RxBufferSize <= MAX_APP_BUFFER_SIZE)
      {
        memcpy(BufferRx, payload, RxBufferSize);
      }
      RssiValue = rssi;
  /* Record payload Signal to noise ratio in Lora*/
     SnrValue = LoraSnr_FskCfo;
     UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);

#endif /* USE_MODEM_LORA | USE_MODEM_FSK */
#if ((USE_MODEM_LORA == 0) && (USE_MODEM_FSK == 1))
  APP_LOG(TS_ON, VLEVEL_L, "RssiValue=%d dBm, Cfo=%dkHz\n\r", rssi, LoraSnr_FskCfo);
  SnrValue = 0; /*not applicable in GFSK*/
#endif /* USE_MODEM_LORA | USE_MODEM_FSK */
  /* Update the State of the FSM*/

  /* USER CODE END OnRxDone */
}

static void OnTxTimeout(void)
{
  /* USER CODE BEGIN OnTxTimeout */
  APP_LOG(TS_ON, VLEVEL_L, "OnTxTimeout\n\r");
  /* Update the State of the FSM*/
  State = TX_TIMEOUT;
  /* Run PingPong process in background*/
  UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
  /* USER CODE END OnTxTimeout */
}

static void OnRxTimeout(void)
{
  /* USER CODE BEGIN OnRxTimeout */
  APP_LOG(TS_ON, VLEVEL_L, "OnRxTimeout\n\r"); //enregistre un message dans les logs pour indiquer qu'un timeout de réception s'est produit.
  /* Update the State of the FSM*/
  State = RX_TIMEOUT;
  /* Run PingPong process in background*/
  UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
  /* USER CODE END OnRxTimeout */
}

static void OnRxError(void)
{
  /* USER CODE BEGIN OnRxError */
  APP_LOG(TS_ON, VLEVEL_L, "OnRxError\n\r"); //enregistre un message dans les logs pour indiquer qu'une erreur de réception s'est produite.
  /* Update the State of the FSM*/
  State = RX_ERROR;
  /* Run PingPong process in background*/
  UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
  /* USER CODE END OnRxError */
}

//------------energy functions -----//
// Get voltage in volts from battery level in mV
static float Voltage_mVToV(uint16_t mV) {
    return ((float)mV) / 1000.0f;
}
static void CalculateEnergy(
    const char* tag,
    float duration_ms,
    float voltage_V,
    float current_A

) {
    float duration_s = duration_ms / 1000.0f;
    float power_W = voltage_V * current_A;
    float energy_J = power_W * duration_s;
    float totalEnergy_J = 0;
    totalEnergy_J += energy_J;

    char duration_str[16];
    char voltage_str[16];
    char current_str[16];
    char power_str[16];
    char energy_str[20];
    char total_energy_str[20];

    snprintf(duration_str, sizeof(duration_str), "%.2f", duration_ms);
    snprintf(voltage_str, sizeof(voltage_str), "%.3f", voltage_V);
    snprintf(current_str, sizeof(current_str), "%.3f", current_A);
    snprintf(power_str, sizeof(power_str), "%.4f", power_W);
    snprintf(energy_str, sizeof(energy_str), "%.6f", energy_J);
    snprintf(total_energy_str, sizeof(total_energy_str), "%.6f", totalEnergy_J);


}


// Log battery voltage drop in mV
static void LogBatteryDrop(const char* tag, uint16_t before_mV, uint16_t after_mV) {
    int16_t drop_mV = (int16_t)(before_mV - after_mV);
    APP_LOG(TS_ON, VLEVEL_M, "%s Battery voltage drop: %d mV (Before: %d mV, After: %d mV)\n\r",
            tag, drop_mV, before_mV, after_mV);
}
void AppendLiveTxMetricsToBuffer(char* buffer, size_t bufferSize, uint32_t startTick, uint16_t startBattery, float current_A, float* totalTxEnergy_J) {
    uint32_t now = HAL_GetTick();
    uint16_t nowBattery = SYS_GetBatteryLevel();

    float duration_ms = (float)(now - startTick);
    float voltage_V = Voltage_mVToV(nowBattery);
    float power_W = voltage_V * current_A;
    float energy_J = power_W * (duration_ms / 1000.0f);
    int16_t drop_mV = (int16_t)(startBattery - nowBattery);

    // Update total energy
    *totalTxEnergy_J += energy_J;

    // Append metrics to buffer
    snprintf(buffer + strlen(buffer),
             bufferSize - strlen(buffer),
             "P=%.4fW;E=%.6fJ;VDrop=%dmV;",
             power_W, energy_J, drop_mV);

    APP_LOG(TS_ON, VLEVEL_L, "Energy metrix for TX:  P=%.4fW, E=%.6fJ,Battery Voltage Drop=%dmV\n\r",
             power_W, energy_J, drop_mV);
}

// Utility to encrypt and base64 encode data (no CRC inside)
static void encrypt_and_encode(const char* plaintext, char* outputBuffer)
{
    uint8_t padded[512] = {0};
    size_t len = strlen(plaintext);
    if (len > sizeof(padded)) len = sizeof(padded);

    // Copy plaintext to padded buffer
    memcpy(padded, plaintext, len);

    // Calculate PKCS7 padding length
    uint8_t pad_len = 16 - (len % 16);
    if (len + pad_len > sizeof(padded)) {
        // Handle error: plaintext too big for buffer with padding
        pad_len = sizeof(padded) - len;
    }

    // Add padding bytes
    for (int i = 0; i < pad_len; i++) {
        padded[len + i] = pad_len;
    }

    size_t padded_len = len + pad_len;

    struct AES_ctx ctx;
    AES_init_ctx(&ctx, aes_key);

    // Encrypt all blocks (padded_len is multiple of 16)
    for (int i = 0; i < padded_len; i += 16)
    {
        AES_ECB_encrypt(&ctx, padded + i);
    }

    // Base64 encode padded_len bytes
    b64_encode(padded, padded_len, (uint8_t*)outputBuffer);
}


// Utility to base64 decode and decrypt data (no CRC check or traiter_commande)
// Utility to base64 decode and decrypt data (no CRC check or traiter_commande)
static int decode_and_decrypt(const char* encoded, char* decrypted_output)
{
    uint8_t decoded[512] = {0};
    int decoded_len = b64_decode((const uint8_t*)encoded, strlen(encoded), decoded);
    if (decoded_len <= 0) return -1;

    // AES decryption (ECB)
    struct AES_ctx ctx;
    AES_init_ctx(&ctx, aes_key);
    for (int i = 0; i < decoded_len; i += 16)
    {
        AES_ECB_decrypt(&ctx, decoded + i);
    }

    // --- PKCS7 Padding removal ---
    uint8_t pad_len = decoded[decoded_len - 1];
    if (pad_len < 1 || pad_len > 16) {
        return -2;  // Invalid padding
    }

    // Validate padding bytes
    bool valid_padding = true;
    for (int i = 0; i < pad_len; i++) {
        if (decoded[decoded_len - 1 - i] != pad_len) {
            valid_padding = false;
            break;
        }
    }
    if (!valid_padding) return -2;

    int unpadded_len = decoded_len - pad_len;

    // Copy clean data and null-terminate
    memcpy(decrypted_output, decoded, unpadded_len);
    decrypted_output[unpadded_len] = '\0';

    return unpadded_len;
}
bool frameTypeSkipsCfEncryption(const char *frame) {
    return (strncmp(frame, "3C;", 3) == 0 || strncmp(frame, "3F;", 3) == 0);
}


static void PingPong_Process(void)
{
  Radio.Sleep(); //met la radio en mode veille pour économiser l'énergie avant de commencer le traitement

  switch (State)
  {
    case RX:
    {

           // UTIL_TIMER_Stop(&timerLed);
            /* Add delay between RX and TX */
    	 // RX complete
    	//sending energy consumed during the rx


            HAL_Delay(Radio.GetWakeupTime() + RX_TIME_MARGIN);  //pour éviter les interférences et permettre à la radio de se réveiller.

            BufferRx[RxBufferSize] = '\0'; // Terminer la chaîne reçue
            APP_LOG(TS_ON, VLEVEL_L, "payload. size = %d \n\r", RxBufferSize);
            APP_LOG(TS_ON, VLEVEL_L, "Données reçues : %s \n\r", BufferRx);
            // --- Decode and decrypt here ---
                                   char decrypted[256] = {0};
                                   int dec_len = decode_and_decrypt((const char*)BufferRx, decrypted);
                                   if (dec_len <= 0) {
                                       APP_LOG(TS_ON, VLEVEL_L, "Déchiffrement/base64 échoué\n\r");
                                       return;
                                   }
                                   strncpy((char*)BufferRx, decrypted, sizeof(BufferRx)-1);
                                   BufferRx[sizeof(BufferRx)-1] = '\0';

                                   APP_LOG(TS_ON, VLEVEL_L, "Payload déchiffré : %s\r\n", BufferRx);


            // 1. Supprimer le dernier point-virgule s’il existe
            size_t len = strlen((char *)BufferRx);
            if (len > 0 && BufferRx[len - 1] == ';') {
                BufferRx[len - 1] = '\0';  // supprime le dernier ';'
            }

            // 2. Trouver le dernier ';' (qui est celui avant le CRC)
            char *last_semicolon = strrchr((char *)BufferRx, ';');

            // 2. Extraire la chaîne CRC reçue (en hex, ex: "1A2B3C4D")
                char crc_str[9] = {0}; // 8 caractères + '\0'
                char *crc_field = last_semicolon + 1; //pointe sur le crc
                strncpy(crc_str, crc_field, 8);
                crc_str[8] = '\0'; // sécurité
             // 3. Convertir le CRC reçu (en hex) en entier
                uint32_t received_crc = (uint32_t)strtoul(crc_str, NULL, 16);
                // 4. Supprimer le CRC de BufferRx → on garde juste la trame pour calcul CRC
                *crc_field = '\0';  // maintenant BufferRx = "3C;A.1;Pompe;0;0;"

                // 5. Calculer CRC avec HAL_CRC_Calculate
                        uint32_t length = strlen((char *)BufferRx);

                        // Calcul CRC depuis un buffer bien aligné
                        __HAL_CRC_DR_RESET(&hcrc);
                        uint32_t calculated_crc = HAL_CRC_Calculate(&hcrc,(uint32_t *) BufferRx, length);

                     // 6. Comparaison
                     if (calculated_crc != received_crc) {
                    	 APP_LOG(TS_ON, VLEVEL_L,"CRC invalide. Calculé: %08X, Reçu: %08X\r\n", calculated_crc, received_crc);
                         return; // Paquet ignoré
                     }

                     // 7. CRC OK, traitement normal
                     APP_LOG(TS_ON, VLEVEL_L,"CRC VALIDE : %08X\r\n", calculated_crc);


                     // Step 4: Handle ACK
                     if (strncmp((char *)BufferRx, "3E;", 3) == 0) {


                         traiter_commande_recue(BufferRx);  // Or whatever handler you have

                         // No RACK or ACK sent for 3E
                         // Optionally, start listening for next RX immediately
                         Radio.Rx(RX_TIMEOUT_VALUE);

                         break;  // Exit switch-case or RX handler here
                     }
                     if (waitingForAck && strncmp((char *)BufferRx, "ACK;", 4) == 0) {
                         ackReceived = true;
                         waitingForAck = false;

                         APP_LOG(TS_ON, VLEVEL_L, "[ACK] Reçu. Exécution de la commande sauvegardée.\n\r");

                         // ✅ EXECUTE the saved command
                         traiter_commande_recue(tempReceivedFrame);

                         // ✅ Send CFs
                         // Prepare confirmation frame plaintext first
                         bool skipEncryption = frameTypeSkipsCfEncryption(tempReceivedFrame);

                         snprintf((char *)BufferTx, MAX_APP_BUFFER_SIZE, "CFs;%s;", app.codDevice);
                         appendTimeToBuffer((char *)BufferTx, HAL_GetTick());
                         AppendCRCToBuffer(&hcrc);

                         if (!skipEncryption) {
                             char encryptedResponse[512] = {0};
                             encrypt_and_encode((const char*)BufferTx, encryptedResponse);
                             strncpy((char*)BufferTx, encryptedResponse, MAX_APP_BUFFER_SIZE - 1);
                             BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';
                         }

                         APP_LOG(TS_ON, VLEVEL_L, "[ACK] Envoi de CFs : %s\n\r", (char *)BufferTx);
                         Radio.Send(BufferTx, strlen((char *)BufferTx));

                         break;
                     }

                     // Step 3: If not ACK, treat as new command → send RACK
                     char frameCopy[MAX_APP_BUFFER_SIZE];
                     strncpy(frameCopy, (char *)BufferRx, sizeof(frameCopy));
                     frameCopy[sizeof(frameCopy)-1] = '\0';

                     char *token = strtok(frameCopy, ";"); // Frame type
                     token = strtok(NULL, ";");            // codDevice
                     if (!token) break;

                     const char *codDevice = token;

                     snprintf((char *)BufferTx, MAX_APP_BUFFER_SIZE, "RACK;%s;", codDevice);
                     appendTimeToBuffer((char *)BufferTx, HAL_GetTick());
                     // Append CRC
                     AppendCRCToBuffer(&hcrc);
                     // Encrypt AFTER CRC
                     char encryptedRACK[256] = {0};
                     encrypt_and_encode((const char*)BufferTx, encryptedRACK);
                     strncpy((char*)BufferTx, encryptedRACK, MAX_APP_BUFFER_SIZE - 1);
                     BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';




                     APP_LOG(TS_ON, VLEVEL_L, "[ACK] Envoi de RACK: %s\n\r", (char *)BufferTx);
                     Radio.Send(BufferTx, strlen((char *)BufferTx));


                     // Save received frame for processing after ACK
                     strncpy(tempReceivedFrame, (char *)BufferRx, sizeof(tempReceivedFrame));
                     waitingForAck = true;

            Radio.Rx(RX_TIMEOUT_VALUE);
                      uint32_t rxEndTime = HAL_GetTick();
                           	            uint16_t batteryAfterRx = SYS_GetBatteryLevel();

                           	            float duration_ms = (float)(rxEndTime - rxStartTime);
                           	            float voltage_V = Voltage_mVToV(batteryAfterRx);

                           	            // Calculate and log energy and power for RX
                           	            CalculateEnergy("RX", duration_ms, voltage_V, RX_CURRENT_A);
                           	            LogBatteryDrop("RX", batteryBeforeRx, batteryAfterRx);

                           	         snprintf(rxEnergyBuffer, sizeof(rxEnergyBuffer),
                           	                  "RX ;DUR:%.2fms;P:%.4fW;E:%.6fJ;",
                           	                  duration_ms, voltage_V * RX_CURRENT_A,
                           	                  (voltage_V * RX_CURRENT_A) * (duration_ms / 1000.0f)
                           	                  );

                           	         appendTimeToBuffer(rxEnergyBuffer, HAL_GetTick());
                           	         AppendCRCToBuffer(&hcrc);
                           	      // --- Encrypt & encode the energy frame ---
                           	      char encryptedEnergy[512] = {0};
                           	      encrypt_and_encode((const char*)rxEnergyBuffer, encryptedEnergy);

                           	      // --- Store the encrypted result in pendingEnergyFrame ---
                           	      strncpy((char*)pendingEnergyFrame, encryptedEnergy, sizeof(pendingEnergyFrame) - 1);
                           	      pendingEnergyFrame[sizeof(pendingEnergyFrame) - 1] = '\0';

                           	      APP_LOG(TS_ON, VLEVEL_L, "Sending RX energy info: %s\n\r", pendingEnergyFrame);
                           	      sendRxEnergyNext = true;
  }
    case WAIT_FOR_TX :
    {


    	 // Start TX timing and battery measurement
    	  txStartTime = HAL_GetTick();
    	  batteryBeforeTx = SYS_GetBatteryLevel();




    	memset(BufferRx, 0, MAX_APP_BUFFER_SIZE);
    	SysTime_t now = SysTimeGet();
    	struct tm currentTime;
    	SysTimeLocalTime(now.Seconds, &currentTime);
    	APP_LOG(TS_ON, VLEVEL_L, "le temps en heure,min et sec %02d:%02d:%02d\n\r", currentTime.tm_hour, currentTime.tm_min, currentTime.tm_sec);

    	if(fistReset){
    		fistReset = 0;
    		snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "3E;%s;%d;%lu;",app.codDevice,utc,(unsigned long) now.Seconds);
    		AppendLiveTxMetricsToBuffer((char*)BufferTx, MAX_APP_BUFFER_SIZE, txStartTime, batteryBeforeTx, TX_CURRENT_A, &totalTxEnergy_J);

    		AppendCRCToBuffer(&hcrc);
    		APP_LOG(TS_ON, VLEVEL_L, "📤 Données avant chiffrement : %s\n\r", (char*)BufferTx);
    		// --- Encrypt BEFORE appending CRC ---
    		        char encryptedPayload[128] = {0};
    		        encrypt_and_encode((const char*)BufferTx, encryptedPayload);
    		        strncpy((char*)BufferTx, encryptedPayload, MAX_APP_BUFFER_SIZE - 1);
    		        BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';


    		APP_LOG(TS_ON, VLEVEL_L, "Commande de synchronisation d'heure est envoyer\n\r");
	    	APP_LOG(TS_ON, VLEVEL_L, " %s\n\r", (char*)BufferTx);

    		Radio.Send(BufferTx, strlen((char*)BufferTx));


    	}
    	if(SensorSendData || danger){
    				battery2 = GetBatteryLevel();
    		    	read_sensor_Data();
    		    	UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_Control_Actuators), CFG_SEQ_Prio_1);
    		    	    SensorSendData = 0;
        				danger = 0;
    		    	    APP_LOG(TS_ON, VLEVEL_L, "Tx start capteurs\n\r");
    		    	    if(app.PS == app.PA){
    		    	    	Actionneur *outputs[] = { &ventilateur, &lampe, &pompe };
    		    	    	Capteur* sensors[] = { &temperature, &humidite, &gaz, &humSol, &lumiere };
    		    	    	buildDeviceDataString((char*)BufferTx, app.codDevice, battery2, sensors, 5, outputs, 3);
    		    	    	appendTimeToBuffer((char*)BufferTx, now.Seconds); //ajout du temps à la fin de la chaine
    		    	    	AppendLiveTxMetricsToBuffer((char*)BufferTx, MAX_APP_BUFFER_SIZE, txStartTime, batteryBeforeTx, TX_CURRENT_A, &totalTxEnergy_J);
    		    	    	AppendCRCToBuffer(&hcrc);
    		    	    	APP_LOG(TS_ON, VLEVEL_L, "📤 Données data avant chiffrement : %s\n\r", (char*)BufferTx);

    		    	    	// --- Encrypt after appending CRC ---
    		    	    	        char encryptedPayload[256] = {0};
    		    	    	        encrypt_and_encode((const char*)BufferTx, encryptedPayload);
    		    	    	        strncpy((char*)BufferTx, encryptedPayload, MAX_APP_BUFFER_SIZE - 1);
    		    	    	        BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';


    		    	    	APP_LOG(TS_ON, VLEVEL_L, "Contenu sensor data à envoyer : %s\n\r", (char*)BufferTx);
    		    	    	Radio.Send(BufferTx, strlen((char*)BufferTx));


    		    	    }else{
    		    	    	// Liste des capteurs à envoyer
    		    	    	Capteur* sensors[] = { &temperature, &humidite, &gaz, &humSol, &lumiere };

    		    	    	 // Construction de la chaîne dans BufferTx
    		    	    	  buildDataString((char*)BufferTx, app, battery2, sensors, 5);
    		    	    	  appendTimeToBuffer((char*)BufferTx, now.Seconds); //ajout du temps à la fin de la chaine
    		    	    	  AppendLiveTxMetricsToBuffer((char*)BufferTx, MAX_APP_BUFFER_SIZE, txStartTime, batteryBeforeTx, TX_CURRENT_A, &totalTxEnergy_J);
    		    	    	  AppendCRCToBuffer(&hcrc);
    		    	    	  APP_LOG(TS_ON, VLEVEL_L, "📤 Données avant chiffrement : %s\n\r", (char*)BufferTx);

    		    	    	  // --- Encrypt after appending CRC ---
    		    	    	          char encryptedPayload[128] = {0};
    		    	    	          encrypt_and_encode((const char*)BufferTx, encryptedPayload);
    		    	    	          strncpy((char*)BufferTx, encryptedPayload, MAX_APP_BUFFER_SIZE - 1);
    		    	    	          BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';


    		    	    	  APP_LOG(TS_ON, VLEVEL_L, "Contenu à envoyer : %s\n\r", (char*)BufferTx);
    		    	    	  Radio.Send(BufferTx, strlen((char*)BufferTx));


    		    	    }

    	}

    	 if (ActuatorSendData) {
    	    battery2 = GetBatteryLevel();  // lecture batterie si besoin
    	    buildActuatorDataString((char*)BufferTx, app.codDevice, battery2);  // construction de la chaîne des actionneurs
    	    appendTimeToBuffer((char*)BufferTx, now.Seconds); //ajout du temps à la fin de la chaine
    	    AppendLiveTxMetricsToBuffer((char*)BufferTx, MAX_APP_BUFFER_SIZE, txStartTime, batteryBeforeTx, TX_CURRENT_A, &totalTxEnergy_J);
    	    APP_LOG(TS_ON, VLEVEL_L, "📤 Données avant chiffrement : %s\n\r", (char*)BufferTx);
    	    AppendCRCToBuffer(&hcrc);
    	    // --- Encrypt after appending CRC ---
    	            char encryptedPayload[128] = {0};
    	            encrypt_and_encode((const char*)BufferTx, encryptedPayload);
    	            strncpy((char*)BufferTx, encryptedPayload, MAX_APP_BUFFER_SIZE - 1);
    	            BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';


    	    APP_LOG(TS_ON, VLEVEL_L, "Contenu à envoyer : %s\n\r", (char*)BufferTx);
    	    Radio.Send(BufferTx, strlen((char*)BufferTx));


    	    ActuatorSendData = 0;
    	}
    	 if(cmd_3F){
    		 battery2 = GetBatteryLevel();
    		 read_sensor_Data();
    		 cmd_3F =0;
    		 Actionneur *outputs[] = { &ventilateur, &lampe, &pompe };
    		 Capteur* sensors[] = { &temperature, &humidite, &gaz, &humSol, &lumiere };
    		 buildDeviceDataString((char*)BufferTx, app.codDevice, battery2, sensors, 5, outputs, 3);
    		 appendTimeToBuffer((char*)BufferTx, now.Seconds); //ajout du temps à la fin de la chaine
    		 AppendLiveTxMetricsToBuffer((char*)BufferTx, MAX_APP_BUFFER_SIZE, txStartTime, batteryBeforeTx, TX_CURRENT_A, &totalTxEnergy_J);
    		 AppendCRCToBuffer(&hcrc);
    		 APP_LOG(TS_ON, VLEVEL_L, "📤 Données avant chiffrement : %s\n\r", (char*)BufferTx);

    		 // --- Encrypt after appending CRC ---
    		         char encryptedPayload[128] = {0};
    		         encrypt_and_encode((const char*)BufferTx, encryptedPayload);
    		         strncpy((char*)BufferTx, encryptedPayload, MAX_APP_BUFFER_SIZE - 1);
    		         BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';


    		 APP_LOG(TS_ON, VLEVEL_L, "Contenu de la commande 3F à envoyer : %s\n\r", (char*)BufferTx);
    		 Radio.Send(BufferTx, strlen((char*)BufferTx));


    	 }
    	 if(cmd_3C){
    		 cmd_3C = 0;
    		 appendTimeToBuffer((char*)BufferTx, now.Seconds); //ajout du temps à la fin de la chaine
    		 AppendLiveTxMetricsToBuffer((char*)BufferTx, MAX_APP_BUFFER_SIZE, txStartTime, batteryBeforeTx, TX_CURRENT_A, &totalTxEnergy_J);
    		 APP_LOG(TS_ON, VLEVEL_L, "📤 Données avant chiffrement : %s\n\r", (char*)BufferTx);
    		 AppendCRCToBuffer(&hcrc);
    		 // --- Encrypt BEFORE appending CRC ---
    		         char encryptedPayload[512] = {0};
    		         encrypt_and_encode((const char*)BufferTx, encryptedPayload);
    		         strncpy((char*)BufferTx, encryptedPayload, MAX_APP_BUFFER_SIZE - 1);
    		         BufferTx[MAX_APP_BUFFER_SIZE - 1] = '\0';


    		 APP_LOG(TS_ON, VLEVEL_L, "Contenu de la commande 3C à envoyer : %s\n\r", (char*)BufferTx);
    		     		 Radio.Send(BufferTx, strlen((char*)BufferTx));


    	 }

    		HAL_Delay(Radio.GetWakeupTime() + RX_TIME_MARGIN);  //pour éviter les interférences et permettre à la radio de se réveiller.


    	break;
    }
    case TX:
    {


    	rxStartTime = HAL_GetTick()
																																																																	;
    	batteryBeforeRx = SYS_GetBatteryLevel();
      APP_LOG(TS_ON, VLEVEL_L, "Rx start\n\r");
      {

        Radio.Rx(RX_TIMEOUT_VALUE);  // starts reception

      }
      break;
    }

    case RX_TIMEOUT:
      {

        Radio.Rx(RX_TIMEOUT_VALUE);

      }
      break;

    case RX_ERROR:


        /* Send the next PING frame */
        /* Add delay between RX and TX*/
        /* add random_delay to force sync between boards after some trials*/
        HAL_Delay(Radio.GetWakeupTime() + RX_TIME_MARGIN + random_delay);  //pour éviter les interférences et permettre à la radio de se réveiller.
        APP_LOG(TS_ON, VLEVEL_L, "Master Tx start error encountred \n\r");
        /* master sends PING*/
        memcpy(BufferTx, Error, sizeof(Error) - 1);
        Radio.Send(BufferTx, PAYLOAD_LEN);



      break;
    case TX_TIMEOUT:


      APP_LOG(TS_ON, VLEVEL_L, "Renvoi des données \n\r");
      Radio.Send(BufferTx, strlen((char*)BufferTx));


      //Radio.Rx(RX_TIMEOUT_VALUE);
      break;
    default:
      break;
  }
}
static void triggerWaitTx (void *context){
	State = WAIT_FOR_TX;
	UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
	APP_LOG(TS_ON, VLEVEL_L, "Déclenchement du timer des capteurs \n\r");
	// Relancer le timer pour la prochaine edxécution
	UTIL_TIMER_Start(&SensorTimer);
	SensorSendData = 1 ;

}

static void OnledEvent(void *context)
{

  HAL_GPIO_TogglePin(LED3_GPIO_Port, LED3_Pin); /* LED_RED */
  UTIL_TIMER_Start(&timerLed);
}

void buildDataString(char* buffer, Device app, uint8_t battery, Capteur* sensors[], int nbSensors) {
    int pos = 0;  //représente la position actuelle d'écriture dans le buffer

    // Écrit dans buffer à partir de buffer + pos : codDevice et batterie
    pos += snprintf(buffer + pos, MAX_APP_BUFFER_SIZE - pos, "%s;%d;", app.codDevice, battery);

    for (int j = 0; j < nbSensors; j++) {
        Capteur* c = sensors[j];
        pos += snprintf(buffer + pos, MAX_APP_BUFFER_SIZE - pos, "%s:%d:%d:%d;",
                        c->typeSensor, c->index, c->value, c->etat);  //pos += ... pour mettre à jour la position
    }
}

static void triggerWaitActuator(void *context) {
    State = WAIT_FOR_TX;
    UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
    APP_LOG(TS_ON, VLEVEL_L, "Déclenchement du timer des actionneurs \n\r");

    // Redémarre le timer
    UTIL_TIMER_Start(&ActuatorTimer);
    ActuatorSendData = 1;
}

void buildActuatorDataString(char *buffer, const char *codDevice, uint8_t battery) {
    sprintf(buffer, "%s;%d;", codDevice, battery);  //Écrit dans le tableau buffer une chaîne de caractères formatée

    Actionneur *outputs[] = { &ventilateur, &lampe, &pompe };  //Crée un tableau de pointeurs vers des objets de type Actionneur
    // taille totale du tableau (en octets)    ///taille d’un élément (un pointeur)
    int nb = sizeof(outputs) / sizeof(outputs[0]); //Calcule combien d’éléments il y a dans le tableau outputs

    for (int i = 0; i < nb; i++) {
        char line[50];
        sprintf(line, "%s:%d:%d;", outputs[i]->output, outputs[i]->index, outputs[i]->etat); //Écrit une chaîne de la forme output:index:etat; dans line
        strcat(buffer, line); //Ajoute le contenu de line à la fin du buffer principal (buffer)
    }
}

void buildDeviceDataString(char *buffer, const char *codDevice, uint8_t battery,
                           Capteur **sensors, int nbSensors,
                           Actionneur **outputs, int nbOutputs) {
    int pos = 0;

    // Ajouter codDevice et batterie
    pos += snprintf(buffer + pos, MAX_APP_BUFFER_SIZE - pos, "%s;%d;", codDevice, battery);

    // Ajouter les capteurs (typeSensor:index:value:etat;)
    for (int i = 0; i < nbSensors; i++) {
        Capteur *c = sensors[i];
        pos += snprintf(buffer + pos, MAX_APP_BUFFER_SIZE - pos, "%s:%d:%d:%d;",
                        c->typeSensor, c->index, c->value, c->etat);
    }

    // Ajouter les actionneurs (output:index:etat;)
    for (int i = 0; i < nbOutputs; i++) {
        Actionneur *a = outputs[i];
        pos += snprintf(buffer + pos, MAX_APP_BUFFER_SIZE - pos, "%s:%d:%d;",
                        a->output, a->index, a->etat);
    }
}

// Fonction utilitaire pour comparer les capteurs
Capteur* find_sensor_by_index_and_type(int index, const char* type) {
    if (strcmp(type, temperature.typeSensor) == 0 && index == temperature.index) return &temperature;
    if (strcmp(type, humidite.typeSensor) == 0 && index == humidite.index) return &humidite;
    if (strcmp(type, gaz.typeSensor) == 0 && index == gaz.index) return &gaz;
    if (strcmp(type, humSol.typeSensor) == 0 && index == humSol.index) return &humSol;
    if (strcmp(type, lumiere.typeSensor) == 0 && index == lumiere.index) return &lumiere;
    return NULL;
}

// Fonction utilitaire pour trouver un actionneur
Actionneur* find_actionneur_by_index_and_output(int index, const char* output) {
    if (strcmp(output, ventilateur.output) == 0 && index == ventilateur.index) return &ventilateur;
    if (strcmp(output, lampe.output) == 0 && index == lampe.index) return &lampe;
    if (strcmp(output, pompe.output) == 0 && index == pompe.index) return &pompe;
    return NULL;
}

// Ajoute le timestamp (heure:minute:seconde) à la fin du buffer
void appendTimeToBuffer(char* buffer, uint32_t timestamp) {
    char timeString[20];  // "_HH:MM:SS" format
    sprintf(timeString, "%lu;", (unsigned long)timestamp);  // Ajoute "_" au début pour le différencier
    strcat(buffer, timeString);
}

void traiter_commande_recue(const char* message) {
    char buffer[100];  //Un buffer temporaire est créé pour copier le message sans modifier l'original
    strncpy(buffer, message, sizeof(buffer)); //copie au maximum 100 caractères.
    buffer[sizeof(buffer) - 1] = '\0'; // assure que la chaîne est bien terminée

    char* token = strtok(buffer, ";"); //extrait le premier morceau de la commande (avant le premier ;)
    if (!token) return;

    char typeCommande[4];
    strncpy(typeCommande, token, sizeof(typeCommande)); //Le type de commande est copié dans typeCommande, avec une taille maximale de 3 caractères
    typeCommande[sizeof(typeCommande) - 1] = '\0'; //+ null terminator

    // Deuxième champ : codDevice
    token = strtok(NULL, ";"); //on passe NULL pour continuer là où ça s'était arrêté
    if (!token) return;
    char* codDeviceRecu = token;

    // Vérifier si cette commande est pour ce device
    if (strcmp(codDeviceRecu, app.codDevice) != 0) {
        APP_LOG(TS_ON, VLEVEL_L, "Commande ignorée, codDevice ne correspond pas\n\r");
        return;
    }

    State = WAIT_FOR_TX;
    UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);

    if (strcmp(typeCommande, "3D") == 0) {  //config appareil
        // Commande pour le device : 3D;codDevice;PS;PA;
        token = strtok(NULL, ";");
        if (token) app.PS = atoi(token); //	Convertit une chaîne en entier
        token = strtok(NULL, ";");
        if (token) app.PA = atoi(token);
        APP_LOG(TS_ON, VLEVEL_L, "Appareil mis à jour: PS=%d, PA=%d\n\r", app.PS, app.PA);
        snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;Appareil est mis a jour;",
                               app.codDevice);
        // Gérer les timers en fonction de la nouvelle config
            if (app.PA == app.PS) {
                // Si PA == PS, on utilise un seul timer -> SensorTimer
                UTIL_TIMER_Stop(&ActuatorTimer); // Stoppe l'autre s’il existe
                APP_LOG(TS_ON, VLEVEL_L, "ActuatorTimer désactivé (PA == PS)\n\r");
            } else {
                // PA != PS, on redémarre ActuatorTimer
                UTIL_TIMER_Stop(&ActuatorTimer); // Stop avant de recréer (sécurité)
                UTIL_TIMER_Create(&ActuatorTimer, app.PA * 60000, UTIL_TIMER_ONESHOT, triggerWaitActuator, NULL);
                UTIL_TIMER_Start(&ActuatorTimer);
                APP_LOG(TS_ON, VLEVEL_L, "ActuatorTimer activé avec PA=%d\n\r", app.PA);
            }

            // Redémarrer toujours le SensorTimer avec la nouvelle PS
            UTIL_TIMER_Stop(&SensorTimer);
            UTIL_TIMER_Create(&SensorTimer, app.PS * 60000, UTIL_TIMER_ONESHOT, triggerWaitTx, NULL);
            UTIL_TIMER_Start(&SensorTimer);
            APP_LOG(TS_ON, VLEVEL_L, "SensorTimer démarré avec PS=%d\n\r", app.PS);
    }
    else if (strcmp(typeCommande, "3E") == 0) {

    	char *timestamp_str = strtok(NULL, ";");  //timeStamp extrait de la trame
    	    if (timestamp_str != NULL) {
    	        uint32_t timestamp = (uint32_t)strtoul(timestamp_str, NULL, 10);  // Utilise strtoul pour éviter les erreurs d'interprétation
    	        SysTime_t sysTime;
    	        sysTime.Seconds = timestamp;
    	        sysTime.SubSeconds = 0;  // Si tu n'utilises pas les fractions de seconde

    	        SysTimeSet(sysTime);  // ✅ Met à jour le temps système avec le bon format
    	        APP_LOG(TS_ON, VLEVEL_L, "TimeStamp synchronisé\n");
    	    } else {
    	        APP_LOG(TS_ON, VLEVEL_L, "[ERREUR] timestamp manquant dans la trame 3E\n");
    	    }
    }
    else if (strcmp(typeCommande, "3S") == 0) {  //config du capteur
        // Commande capteur : 3S;codDevice;typeSensor;index;alertD;normalD;alertN;normalN;
        char* typeSensor = strtok(NULL, ";");
        int index = atoi(strtok(NULL, ";"));
        int alertD = atoi(strtok(NULL, ";"));
        int normalD = atoi(strtok(NULL, ";"));
        int alertN = atoi(strtok(NULL, ";"));
        int normalN = atoi(strtok(NULL, ";"));

        Capteur* capteur = find_sensor_by_index_and_type(index, typeSensor); // On cherche un pointeur vers le capteur correspondant au type fourni
        if (capteur) {
            capteur->alertThershold = (float)alertD;
            capteur->normalThershold = (float)normalD;
            if (strcmp(typeSensor, "temperature") == 0 || strcmp(typeSensor, "humidite") == 0) {
                // Si le capteur est température ou humidité, on applique aussi alertN et normalN
                capteur->alertThersholdN = (float)alertN;
                capteur->normalThersholdN = (float)normalN;
            }
            APP_LOG(TS_ON, VLEVEL_L, "Capteur %s mis à jour\n\r", capteur->typeSensor);
            snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;Capteur %s est mis a jour;",
                                                   app.codDevice,capteur->typeSensor);
        } else {
            APP_LOG(TS_ON, VLEVEL_L, "Capteur %s non trouvé\n\r", typeSensor);
            snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;Capteur %s non trouve;",
                                                               app.codDevice,capteur->typeSensor);
        }
    }
    else if (strcmp(typeCommande, "3F") == 0) { //get Data
    	cmd_3F = 1;

    }
    else if (strcmp(typeCommande, "3C") == 0){  //do action
        char* output = strtok(NULL, ";");
        if (!output) return;

        char* etatStr = strtok(NULL, ";");
        if (!etatStr) return;
        int etat = atoi(etatStr);

        char* indexStr = strtok(NULL, ";");
        if (!indexStr) return;
        int index = atoi(indexStr);

        cmd_3C = 1;
        Actionneur* actionneur = find_actionneur_by_index_and_output(index, output);
        if(actionneur){
            actionneur->etat = etat;  // MAJ de l'état dans la structure

            snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;%s:%d:%d;",
                       app.codDevice, actionneur->output, actionneur->etat, actionneur->index);
             cmd_3C = 1;

            if(etat == 1){
                APP_LOG(TS_ON, VLEVEL_L, "Actionneur %s est en marche \n\r", actionneur->output);
                HAL_GPIO_WritePin(actionneur->port, actionneur->pin, GPIO_PIN_RESET);
            }
            else{
            	HAL_GPIO_WritePin(actionneur->port, actionneur->pin, GPIO_PIN_SET);
                APP_LOG(TS_ON, VLEVEL_L, "Actionneur %s est arrete \n\r", actionneur->output);
            }
        } else {
            APP_LOG(TS_ON, VLEVEL_L, "Actionneur de type %s est non trouvé \n\r", output);
        }

    }
    else if (strcmp(typeCommande, "3A") == 0) {
        // 3A;codDevice;output;index;sensor1_type;sensor1_index;sensor2_type;sensor2_index;...
        token = strtok(NULL, ";");
        if (!token) return;
        char* output = token;

        token = strtok(NULL, ";");
        if (!token) return;
        int actionneurIndex = atoi(token);

        // Chercher l'actionneur correspondant
        Actionneur* act = find_actionneur_by_index_and_output(actionneurIndex, output);
        if (!act) {
            APP_LOG(TS_ON, VLEVEL_L, "Actionneur non trouvé: %s[%d]\n\r", output, actionneurIndex);
            snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;Actionneur %s non trouve;",
                                                                           app.codDevice,output);
            return;
        }

        // Collecte temporaire des capteurs à associer
        Capteur* capteursAssocies[10];
        int nbCapteursAssocies = 0;

        while ((token = strtok(NULL, ";")) != NULL) {
            char* sensorType = token;
            token = strtok(NULL, ";");
            if (!token) break;
            int sensorIndex = atoi(token);

            Capteur* capteur = find_sensor_by_index_and_type(sensorIndex, sensorType);
            if (!capteur) {
                APP_LOG(TS_ON, VLEVEL_L, "Capteur non trouvé: %s[%d]\n\r", sensorType, sensorIndex);
                snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;Capteur non trouve: %s;",
                                                                                           app.codDevice,sensorType);
                continue;
            }

            // Vérifier si l'actionneur est déjà associé
            bool dejaAssocie = false;
            for (int i = 0; i < capteur->nbActionneurs; i++) {
                if (capteur->actionneursAssocies[i]->index == act->index &&
                	    strcmp(capteur->actionneursAssocies[i]->output, act->output) == 0) {
                    dejaAssocie = true;
                    APP_LOG(TS_ON, VLEVEL_L, "Capteur déja associé : %s[%d]\n\r", sensorType, sensorIndex);
                    break;
                }
            }

            if (!dejaAssocie && capteur->nbActionneurs < MAX_ACTIONNEURS_PAR_CAPTEUR) {
                capteur->actionneursAssocies[capteur->nbActionneurs++] = act;
                APP_LOG(TS_ON, VLEVEL_L, "Actionneur %s associé à capteur %s[%d]\n\r", act->output, capteur->typeSensor, capteur->index);
            }

            capteursAssocies[nbCapteursAssocies++] = capteur;
        }

        // Nettoyage : retirer l'actionneur des capteurs non mentionnés
        Capteur* tousCapteurs[] = { &temperature, &humidite, &gaz, &humSol, &lumiere };
        for (int i = 0; i < sizeof(tousCapteurs)/sizeof(tousCapteurs[0]); i++) {
            Capteur* cap = tousCapteurs[i];
            bool estMentionne = false;
            for (int j = 0; j < nbCapteursAssocies; j++) {
            	//verifie si le capteur existe dans capteursAssocies alors pas besoin de verifier le reste
                if (cap->index == capteursAssocies[j]->index &&
                	    strcmp(cap->typeSensor, capteursAssocies[j]->typeSensor) == 0) {
                    estMentionne = true;
                    break;
                }
            }
            //capteur n'existe pas dans capteursAssocies
            if (!estMentionne) {
                // Supprimer l'actionneur si présent
                for (int j = 0; j < cap->nbActionneurs; j++) {
                    if (cap->actionneursAssocies[j]->index == act->index &&
                    		strcmp(cap->actionneursAssocies[j]->output, act->output) == 0) {
                        for (int k = j; k < cap->nbActionneurs - 1; k++) {
                            cap->actionneursAssocies[k] = cap->actionneursAssocies[k + 1];
                        }
                        cap->nbActionneurs--;
                        APP_LOG(TS_ON, VLEVEL_L, "Actionneur %s dissocié de capteur %s[%d]\n\r", act->output, cap->typeSensor, cap->index);
                        break;
                    }
                }
            }
        }
        snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;Actionneur %s est mis a jour;",app.codDevice,output);
    }
    else {
        APP_LOG(TS_ON, VLEVEL_L, "Type de commande inconnu : %s\n\r", typeCommande);
        snprintf((char*)BufferTx, MAX_APP_BUFFER_SIZE, "%s;Commande inconnu;",app.codDevice);
    }
}

// Fonction pour calculer et ajouter le CRC à la fin de BufferTx
void AppendCRCToBuffer(CRC_HandleTypeDef *hcrc)
{
    // 1. Calculer la longueur du texte existant
    uint32_t length = strlen((char *)BufferTx);

    // 2. Calculer le CRC (en précisant bien le format en octets)
    uint32_t crc = HAL_CRC_Calculate(hcrc, (uint32_t *)BufferTx, length);

    // 3. Convertir le CRC en texte hexadécimal
    char crcStr[9]; // 8 caractères + null terminator
    snprintf(crcStr, sizeof(crcStr), "%08lX", crc); // Uppercase hex

    // 4. Ajouter le ";" au buffer
    if (length + 10 < MAX_APP_BUFFER_SIZE)
        {
    	BufferTx[length + 8] = ';';
            memcpy(&BufferTx[length], crcStr, 8);     // Ajoute les 8 caractères CRC
                         // Ajoute le point-virgule final
            BufferTx[length + 9] = '\0';              // Ajoute le null terminator
        }
    else
    {
        // Gérer le cas d'erreur si dépassement de buffer
    	APP_LOG(TS_ON, VLEVEL_L, "Dépassement d'espace pour le buffer \n\r");
    }
}
