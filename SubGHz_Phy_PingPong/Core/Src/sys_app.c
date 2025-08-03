/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file    sys_app.c
  * @author  MCD Application Team
  * @brief   Initializes HW and SW system entities (not related to the radio)
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
#include <stdio.h>
#include "platform.h"
#include "sys_app.h"
#include "stm32_seq.h"
#include "stm32_systime.h"
#include "stm32_lpm.h"
#include "timer_if.h"
#include "utilities_def.h"
#include "sys_debug.h"
#include "aes.h"
#include "base64.h"

/* USER CODE BEGIN Includes */
#include "adc_if.h"
#include "bme680.h"
#include "BH1750.h"
#include "subghz_phy_app.h"
#include "stm32_timer.h"
/* USER CODE END Includes */

/* External variables ---------------------------------------------------------*/
/* USER CODE BEGIN EV */
extern I2C_HandleTypeDef hi2c2;
/* USER CODE END EV */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

struct tm date = {
	  .tm_year = 2025 - 1900,  // année - 1900
	  .tm_mon = 5,             // avril (0 = janv, donc 3 = avril)
	  .tm_mday = 13,
	  .tm_hour = 12,
	  .tm_min = 22,
	  .tm_sec = 0
	};
/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
#define MAX_TS_SIZE (int) 16

/* USER CODE BEGIN PD */
/**
  * Defines the maximum battery level
  */
#define LORA_MAX_BAT   254
/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
static uint8_t SYS_TimerInitialisedFlag = 0;

/* USER CODE BEGIN PV */
struct bme680_dev gas_sensor;
struct bme680_field_data data;
int8_t rslt = BME680_OK;
int8_t rslt_secondary = BME680_OK;
int attempt_count = 0;
uint16_t humidity =0;
uint16_t gaz2 =0;
uint16_t luz=0;
uint16_t humSol2=0;
uint16_t temperaturee;
int tempOptimale = 0;  //diurne
int humOptimale =0;    //diurne
int tempOptimaleN = 0;  //nocture
int humOptimaleN =0;  //nocturne
int solOptimale =0;
int lumOptimale =0;
int fanbyHum = 0;
int fanbyTemp = 0;
uint8_t danger = 0;
// Variables globales du structure
Actionneur ventilateur;
Actionneur lampe;
Actionneur pompe;

Capteur temperature;
Capteur humidite;
Capteur gaz;
Capteur humSol;
Capteur lumiere;

Device app;

SysTime_t now;
struct tm Time;
static UTIL_TIMER_Object_t gazTimer;
/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
/**
  * @brief Returns sec and msec based on the systime in use
  * @param buff to update with timestamp
  * @param size of updated buffer
  */
static void TimestampNow(uint8_t *buff, uint16_t *size);

/**
  * @brief  it calls UTIL_ADV_TRACE_VSNPRINTF
  */
static void tiny_snprintf_like(char *buf, uint32_t maxsize, const char *strFormat, ...);

/* USER CODE BEGIN PFP */

/* USER CODE END PFP */

/* Exported functions ---------------------------------------------------------*/
void SystemApp_Init(void)
{
  /* USER CODE BEGIN SystemApp_Init_1 */

  /* USER CODE END SystemApp_Init_1 */

  /* Ensure that MSI is wake-up system clock */
  __HAL_RCC_WAKEUPSTOP_CLK_CONFIG(RCC_STOP_WAKEUPCLOCK_MSI);

  /*Initialize timer and RTC*/
  UTIL_TIMER_Init();
  SYS_TimerInitialisedFlag = 1;
  /* Initializes the SW probes pins and the monitor RF pins via Alternate Function */
  DBG_Init();

  /*Initialize the terminal */
  UTIL_ADV_TRACE_Init();
  UTIL_ADV_TRACE_RegisterTimeStampFunction(TimestampNow);

  /*Set verbose LEVEL*/
  UTIL_ADV_TRACE_SetVerboseLevel(VERBOSE_LEVEL);

  /*Init low power manager*/
  UTIL_LPM_Init();
  /* Disable Stand-by mode */
  UTIL_LPM_SetOffMode((1 << CFG_LPM_APPLI_Id), UTIL_LPM_DISABLE);

#if defined (LOW_POWER_DISABLE) && (LOW_POWER_DISABLE == 1)
  /* Disable Stop Mode */
  UTIL_LPM_SetStopMode((1 << CFG_LPM_APPLI_Id), UTIL_LPM_DISABLE);
#elif !defined (LOW_POWER_DISABLE)
#error LOW_POWER_DISABLE not defined
#endif /* LOW_POWER_DISABLE */

  /* USER CODE BEGIN SystemApp_Init_2 */

  /* USER CODE END SystemApp_Init_2 */
}

/**
  * @brief redefines __weak function in stm32_seq.c such to enter low power
  */
void UTIL_SEQ_Idle(void)
{
  /* USER CODE BEGIN UTIL_SEQ_Idle_1 */

  /* USER CODE END UTIL_SEQ_Idle_1 */
  UTIL_LPM_EnterLowPower();
  /* USER CODE BEGIN UTIL_SEQ_Idle_2 */

  /* USER CODE END UTIL_SEQ_Idle_2 */
}

/* USER CODE BEGIN EF */
uint8_t GetBatteryLevel(void)
{
  uint8_t batteryLevel = 0;
  uint16_t batteryLevelmV;

  /* USER CODE BEGIN GetBatteryLevel_0 */

  /* USER CODE END GetBatteryLevel_0 */

  batteryLevelmV = (uint16_t) SYS_GetBatteryLevel();

  /* Convert battery level from mV to linear scale: 1 (very low) to 254 (fully charged) */
  if (batteryLevelmV > VDD_BAT)
  {
    batteryLevel = LORA_MAX_BAT;
  }
  else if (batteryLevelmV < VDD_MIN)
  {
    batteryLevel = 0;
  }
  else
  {
    batteryLevel = (((uint32_t)(batteryLevelmV - VDD_MIN) * LORA_MAX_BAT) / (VDD_BAT - VDD_MIN));
  }

  APP_LOG(TS_ON, VLEVEL_M, "VDDA= %d\r\n", batteryLevel);

  /* USER CODE BEGIN GetBatteryLevel_2 */

  /* USER CODE END GetBatteryLevel_2 */

  return batteryLevel;  /* 1 (very low) to 254 (fully charged) */
}

uint16_t GetTemperatureLevel(void)
{
  uint16_t temperatureLevel = 0;

  temperatureLevel = (uint16_t)(SYS_GetTemperatureLevel() / 256);
  /* USER CODE BEGIN GetTemperatureLevel */

  /* USER CODE END GetTemperatureLevel */
  return temperatureLevel;
}

/* USER CODE END EF */

/* Private functions ---------------------------------------------------------*/

static void TimestampNow(uint8_t *buff, uint16_t *size)
{
  /* USER CODE BEGIN TimestampNow_1 */

  /* USER CODE END TimestampNow_1 */
  SysTime_t curtime = SysTimeGet();
  tiny_snprintf_like((char *)buff, MAX_TS_SIZE, "%ds%03d:", curtime.Seconds, curtime.SubSeconds);
  *size = strlen((char *)buff);
  /* USER CODE BEGIN TimestampNow_2 */

  /* USER CODE END TimestampNow_2 */
}

/* Disable StopMode when traces need to be printed */
void UTIL_ADV_TRACE_PreSendHook(void)
{
  /* USER CODE BEGIN UTIL_ADV_TRACE_PreSendHook_1 */

  /* USER CODE END UTIL_ADV_TRACE_PreSendHook_1 */
  UTIL_LPM_SetStopMode((1 << CFG_LPM_UART_TX_Id), UTIL_LPM_DISABLE);
  /* USER CODE BEGIN UTIL_ADV_TRACE_PreSendHook_2 */

  /* USER CODE END UTIL_ADV_TRACE_PreSendHook_2 */
}
/* Re-enable StopMode when traces have been printed */
void UTIL_ADV_TRACE_PostSendHook(void)
{
  /* USER CODE BEGIN UTIL_LPM_SetStopMode_1 */

  /* USER CODE END UTIL_LPM_SetStopMode_1 */
  UTIL_LPM_SetStopMode((1 << CFG_LPM_UART_TX_Id), UTIL_LPM_ENABLE);
  /* USER CODE BEGIN UTIL_LPM_SetStopMode_2 */

  /* USER CODE END UTIL_LPM_SetStopMode_2 */
}

static void tiny_snprintf_like(char *buf, uint32_t maxsize, const char *strFormat, ...)
{
  /* USER CODE BEGIN tiny_snprintf_like_1 */

  /* USER CODE END tiny_snprintf_like_1 */
  va_list vaArgs;
  va_start(vaArgs, strFormat);
  UTIL_ADV_TRACE_VSNPRINTF(buf, maxsize, strFormat, vaArgs);
  va_end(vaArgs);
  /* USER CODE BEGIN tiny_snprintf_like_2 */

  /* USER CODE END tiny_snprintf_like_2 */
}

/* USER CODE BEGIN PrFD */
int8_t user_i2c_read(uint8_t dev_id, uint8_t reg_addr, uint8_t *data, uint16_t len)
{
    // Implement I2C read function
    return HAL_I2C_Mem_Read(&hi2c2, dev_id << 1, reg_addr, I2C_MEMADD_SIZE_8BIT, data, len, HAL_MAX_DELAY);
}

int8_t user_i2c_write(uint8_t dev_id, uint8_t reg_addr, uint8_t *data, uint16_t len)
{
    // Implement I2C write function
    return HAL_I2C_Mem_Write(&hi2c2, dev_id << 1, reg_addr, I2C_MEMADD_SIZE_8BIT, data, len, HAL_MAX_DELAY);
}

void user_delay_ms(uint32_t period)
{
    HAL_Delay(period);
}

void BME680_Init(){

	      gas_sensor.dev_id = BME680_I2C_ADDR_PRIMARY; // 0x76
	      gas_sensor.intf = BME680_I2C_INTF;
	      gas_sensor.read = user_i2c_read;
	      gas_sensor.write = user_i2c_write;
	      gas_sensor.delay_ms = user_delay_ms;
	      gas_sensor.amb_temp = 25;


	      // Try initializing with primary I2C address
	      while (attempt_count < 5)
	      {
	          rslt = bme680_init(&gas_sensor);

	          if (rslt == BME680_OK)
	          {
	              break;
	          }
	          attempt_count++;
	          HAL_Delay(200);
	      }

	      // If primary address fails, try secondary I2C address
	      if (rslt != BME680_OK)
	      {
	          gas_sensor.dev_id = BME680_I2C_ADDR_SECONDARY; // 0x77
	          attempt_count = 0;
	          while (attempt_count < 5)
	          {
	              rslt_secondary = bme680_init(&gas_sensor);

	              if (rslt_secondary == BME680_OK)
	              {
	                  break;
	              }
	              attempt_count++;
	              HAL_Delay(200);
	          }
	      }

	      if (rslt == BME680_OK || rslt_secondary == BME680_OK)
	      {
	          APP_LOG(TS_ON, VLEVEL_L, "BME680 initialized successfully\n\r");
	      }
	      else
	      {
	          APP_LOG(TS_ON, VLEVEL_L, "BME680 initialization failed\n\r");
	          while (1); // Halt execution
	      }
	      gas_sensor.tph_sett.os_hum = BME680_OS_2X; //BME680_OS_16X for maximum 16 average samplings
	      gas_sensor.tph_sett.os_pres = BME680_OS_4X; //BME680_OS_16X for maximum 16 average samplings
	      gas_sensor.tph_sett.os_temp = BME680_OS_8X; //BME680_OS_16X for maximum 16 average samplings
	      gas_sensor.tph_sett.filter = BME680_FILTER_SIZE_3; //BME680_FILTER_SIZE_127 max IIR filter setting

	      gas_sensor.gas_sett.run_gas = BME680_ENABLE_GAS_MEAS; //BME680_DISABLE_GAS_MEAS to disabled GAS measurements (to save power)
	      gas_sensor.gas_sett.heatr_temp = 320; // Target temperature in degrees Celsius
	      gas_sensor.gas_sett.heatr_dur = 150; // Heating duration in milliseconds

	      gas_sensor.power_mode = BME680_FORCED_MODE; //BME680_SLEEP_MODE

	      // Define desired settings
	      uint16_t desired_settings = BME680_OST_SEL | BME680_OSP_SEL | BME680_OSH_SEL | BME680_FILTER_SEL | BME680_GAS_SENSOR_SEL;

	      rslt = bme680_set_sensor_settings(desired_settings, &gas_sensor);
	      if (rslt != BME680_OK) {
	          APP_LOG(TS_ON, VLEVEL_L, "Failed to set sensor settings\n\r");
	      }
	      uint16_t meas_period;
	          bme680_get_profile_dur(&meas_period, &gas_sensor);
}
void initialize_struct(void){
	 ventilateur = (Actionneur) {0, "Ventilateur", GPIOA, GPIO_PIN_0,0};
	 lampe = (Actionneur) {0, "Lampe", GPIOB, GPIO_PIN_10,0};
	 pompe = (Actionneur) {0, "Pompe", GPIOA, GPIO_PIN_9,0};

	 app = (Device){"A.1", 2,2 };

	           temperature = (Capteur) {
	              .index = 0,
	              .typeSensor = "temperature",
	              .value = 0,
	              .alertThershold = 35.0,
	              .normalThershold = 21.0,
				  .alertThersholdN = 30.0,
				  .normalThersholdN = 16.0,
	              .nbActionneurs = 2,
				  .etat = 1
	          };
	           humidite = (Capteur) {
	                        .index = 0,
	                        .typeSensor = "humidite",
	                        .value = 0,
	                        .alertThershold = 85.0,
	                        .normalThershold = 30.0,
							.alertThersholdN = 75.0,
							.normalThersholdN = 20.0,
	                        .nbActionneurs = 2,
							.etat = 1
	           };

	           gaz = (Capteur) {
	                                  .index = 0,
	                                  .typeSensor = "gaz",
	                                  .value = 0,
	                                  .alertThershold = 500.0,
	                                  .normalThershold = 0.0,
									  .alertThersholdN = 0.0,
									  .normalThersholdN = 0.0,
	                                  .nbActionneurs = 1,
									  .etat = 1
	                     };

	           humSol = (Capteur) {
	                                            .index = 0,
	                                            .typeSensor = "humSol",
	                                            .value = 0,
	                                            .alertThershold = 3000.0,
	                                            .normalThershold = 1000.0,
												.alertThersholdN = 0.0,
												.normalThersholdN = 0.0,
	                                            .nbActionneurs = 1,
												.etat = 1
	                               };
	           lumiere =  (Capteur){
	                                                      .index = 0,
	                                                      .typeSensor = "lumiere",
	                                                      .value = 0,
	                                                      .alertThershold = 100.0,
	                                                      .normalThershold = 0.0,
														  .alertThersholdN = 0.0,
														  .normalThersholdN = 0.0,
	                                                      .nbActionneurs = 1,
														  .etat = 1
	                                         };

	          // On associe les actionneurs au capteur
	          temperature.actionneursAssocies[0] = &ventilateur;
	          temperature.actionneursAssocies[1] = &lampe;
	          humidite.actionneursAssocies[0] = &ventilateur;
	          humidite.actionneursAssocies[1] = &pompe;
	          gaz.actionneursAssocies[0] = &ventilateur;
	          humSol.actionneursAssocies[0] = &pompe;
	          lumiere.actionneursAssocies[0] = &lampe;
}

void controlActionneurByType(Capteur* capteur, const char* type, GPIO_PinState state) {
    for (int i = 0; i < capteur->nbActionneurs; i++) {
        Actionneur* act = capteur->actionneursAssocies[i];
        if (strcmp(act->output, type) == 0) {
            HAL_GPIO_WritePin(act->port, act->pin, state);
        }
    }
}

void read_sensor_Data(){
	//temperaturee = (SYS_GetTemperatureLevel() >> 8);
	 now = SysTimeGet();
	SysTimeLocalTime(now.Seconds, &Time);

	rslt = bme680_get_sensor_data(&data, &gas_sensor);
		              if (rslt == BME680_OK)
		              {
		            	  temperaturee = data.temperature / 100.0;
		            	  temperature.value = temperaturee;
		                  humidity = data.humidity / 1000.0;
		                  humidite.value = data.humidity / 1000.0;
		                  gaz2 = data.gas_resistance / 1000.0;
		                  gaz.value = data.gas_resistance / 1000.0;
		                  APP_LOG(TS_ON, VLEVEL_L, "humidité %d rh \n\r",humidity );
		                  APP_LOG(TS_ON, VLEVEL_L, "gaz %d kOhms\n\r",gaz2 );
		                  APP_LOG(TS_ON, VLEVEL_L, "temperature %d °C\n\r",temperaturee );
		              }

		              if (gas_sensor.power_mode == BME680_FORCED_MODE)
		              {
		                  rslt = bme680_set_sensor_mode(&gas_sensor);
		              }
		              luz =BH1750_Lumen(continua_LO_1);
		              lumiere.value = luz;
		              APP_LOG(TS_ON, VLEVEL_L, "lumière en %d lux\n\r",luz );
		              humSol2 = humSolData();
		              humSol.value = humSol2;
		              APP_LOG(TS_ON, VLEVEL_L, "humidité du sol  %d \n\r",humSol2 );
		              //remplissage des etats des capteurs
		              solOptimale = (humSol.value >= humSol.normalThershold && humSol.value <= humSol.alertThershold); humSol.etat = solOptimale;
		              gaz.etat = (gaz.value <= gaz.alertThershold);
		              if(Time.tm_hour < 18){
		            	  lumOptimale = (lumiere.value >= lumiere.alertThershold); lumiere.etat = lumOptimale;
		            	  tempOptimale= (temperature.value >= temperature.normalThershold && temperature.value <= temperature.alertThershold); temperature.etat= tempOptimale;
		            	  humOptimale = (humidite.value >= humidite.normalThershold && humidite.value <= humidite.alertThershold); humidite.etat = humOptimale;
		              }else{
		            	  tempOptimaleN= (temperature.value >= temperature.normalThersholdN && temperature.value <= temperature.alertThersholdN); temperature.etat= tempOptimaleN;
		            	  humOptimaleN = (humidite.value >= humidite.normalThersholdN && humidite.value <= humidite.alertThersholdN); humidite.etat = humOptimaleN;
		            	  lumOptimale =1;
		              }

}

void control_actuators(){

								UTIL_TIMER_Stop(&gazTimer);
		                       //gestion du gaz
				              if(gaz.value >= gaz.alertThershold){
				            	  APP_LOG(TS_ON, VLEVEL_L, "Détectement de dépassement de seuil pour le capteur du gaz \n\r");
				            	  controlActionneurByType(&gaz, "Ventilateur", GPIO_PIN_SET); //tafi
				            	  controlActionneurByType(&temperature, "Lampe", GPIO_PIN_SET); //tafi
				            	  controlActionneurByType(&humSol, "Pompe", GPIO_PIN_SET); //tafi
				            	  ventilateur.etat = 0;
				            	  lampe.etat =0;
				            	  pompe.etat = 0;
				            	  if(app.PS > 2 ){
				            		  UTIL_TIMER_Create(&gazTimer, 60000 , UTIL_TIMER_ONESHOT, gazCallBack, NULL);
				            		  UTIL_TIMER_Start(&gazTimer);
				            		  danger = 1;
				            	  }

				            	 return;
				              }

				              if(Time.tm_hour < 18){
				            	  //gestion du temperature et humidite
				            	  	if (!tempOptimale || !humOptimale) {   //temp et humidite tres elevees
				            	  			if(temperature.value >= temperature.alertThershold || humidite.value >= humidite.alertThershold){
				            	  				     controlActionneurByType(&temperature, "Ventilateur", GPIO_PIN_RESET);
				            	  			          ventilateur.etat = 1;
				            	  				      fanbyHum = 1;
				            	  				      if(temperature.value >= temperature.alertThershold){
				            	  				            	fanbyHum = 0;
				            	  				            	fanbyTemp = 1;
				            	  				            	controlActionneurByType(&temperature, "Lampe", GPIO_PIN_SET);
				            	  				            	lampe.etat = 0;
				            	  				          }
				            	  				           else {
				            	  				            	fanbyTemp = 0;
				            	  				            }

				            	  				        }
				            	  				            	  //humidite et temp tres basses
				            	  			else if(temperature.value <= temperature.normalThershold || humidite.value <= humidite.normalThershold){
				            	  				       if(temperature.value <= temperature.normalThershold ){
				            	  				            controlActionneurByType(&temperature, "Lampe", GPIO_PIN_RESET);
				            	  				            lampe.etat = 1;
				            	  				            // controlActionneurByType(&temperature, "ventilateur", GPIO_PIN_SET);
				            	  				            }
				            	  				        if( humidite.value <= humidite.normalThershold){
				            	  				            	if(solOptimale){
				            	  				            		controlActionneurByType(&humidite, "Pompe", GPIO_PIN_RESET); //reset tcha3el el pompe
				            	  				            		pompe.etat = 1;
				            	  				            			  }
				            	  				            		  }
				            	  				            	  }
				            	  				              }
				            	  				if((humOptimale && fanbyHum) ||(tempOptimale && fanbyTemp)){
				            	  					    controlActionneurByType(&humidite, "Ventilateur", GPIO_PIN_SET);
				            	  					    ventilateur.etat = 0;
				            	  				}

				            	  				if(tempOptimale){

				            	  				      if(lumiere.value <= lumiere.alertThershold){
				            	  				            	controlActionneurByType(&lumiere, "Lampe", GPIO_PIN_RESET);
				            	  				            	lampe.etat =1;
				            	  				       }
				            	  				        else {
				            	  				            	controlActionneurByType(&lumiere, "Lampe", GPIO_PIN_SET);
				            	  				            	lampe.etat = 0;
				            	  				          }
				            	  				    }
				            	  				//debut du time > 18
				              	  	  	  	  	  }else{
				              	  	  	  	  		  	  if (!tempOptimaleN || !humOptimaleN) {   //temp et humidite tres elevees
				              	  	  	  	  				   if(temperature.value >= temperature.alertThersholdN || humidite.value >= humidite.alertThersholdN){
				              	  	  	  	  				         controlActionneurByType(&temperature, "Ventilateur", GPIO_PIN_RESET);
				              	  	  	  	  				         ventilateur.etat = 1;
				              	  	  	  	  				         fanbyHum = 1;
				              	  	  	  	  				         if(temperature.value >= temperature.alertThersholdN){
				              	  	  	  	  				            	fanbyHum = 0;
				              	  	  	  	  				            	fanbyTemp = 1;
				              	  	  	  	  				            	controlActionneurByType(&temperature, "Lampe", GPIO_PIN_SET);
				              	  	  	  	  				            	lampe.etat = 0;
				              	  	  	  	  				           }
				              	  	  	  	  				           else {
				              	  	  	  	  				            	  fanbyTemp = 0;
				              	  	  	  	  				           }

				              	  	  	  	  				   }
				              	  	  	  	  				    //humidite et temp tres basses
				              	  	  	  	  				   else if(temperature.value <= temperature.normalThersholdN || humidite.value <= humidite.normalThersholdN){
				              	  	  	  	  				          if(temperature.value <= temperature.normalThersholdN ){
				              	  	  	  	  				        	  controlActionneurByType(&temperature, "Lampe", GPIO_PIN_RESET);
				              	  	  	  	  				               lampe.etat = 1;
				              	  	  	  	  				               // controlActionneurByType(&temperature, "ventilateur", GPIO_PIN_SET);
				              	  	  	  	  				            }
				              	  	  	  	  				            if( humidite.value <= humidite.normalThersholdN){
				              	  	  	  	  				            	 if(solOptimale){
				              	  	  	  	  				            	  	controlActionneurByType(&humidite, "Pompe", GPIO_PIN_RESET); //reset tcha3el el pompe
				              	  	  	  	  				            	  	pompe.etat = 1;
				              	  	  	  	  				            	  	}
				              	  	  	  	  				            }
				              	  	  	  	  				    }
				              	  	  	  	  			}
				              	  	  	  	  	if((humOptimaleN && fanbyHum) ||(tempOptimaleN && fanbyTemp)){
				              	  	  	  	  		controlActionneurByType(&humidite, "Ventilateur", GPIO_PIN_SET);
				              	  	  	  	  		ventilateur.etat = 0;
				              	  	  	  	  	}
				              	  	  	  	  	if(tempOptimaleN){
				              	  	  	  	  		controlActionneurByType(&lumiere, "Lampe", GPIO_PIN_SET);
				              	  	  	  			lampe.etat = 0;
				              	  	  	  	  	}

				              	  	  	  	  	  }

				             //gestion d'uimidité du sol
				             if(humSol.value >= humSol.alertThershold){
				            	 controlActionneurByType(&humSol, "Pompe", GPIO_PIN_RESET);
				            	 pompe.etat = 1;
				             }
				             else if(humSol.value <= humSol.normalThershold ){
				            	 controlActionneurByType(&humSol, "Pompe", GPIO_PIN_SET);
				            	 pompe.etat = 0;
				             }
				             HAL_Delay(500);

		if(Time.tm_hour < 18){
			if( !(tempOptimale && humOptimale && solOptimale && lumOptimale)){
							 read_sensor_Data();

							 UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_Control_Actuators), CFG_SEQ_Prio_1);
					}

		else{
			tempOptimale =0; humOptimale =0; solOptimale=0; lumOptimale= 0;
							             	fanbyHum = 0;
							             	fanbyTemp = 0;
			ventilateur.etat = 0;
			lampe.etat =0;
			pompe.etat = 0;
			controlActionneurByType(&gaz, "Ventilateur", GPIO_PIN_SET); //tafi
			controlActionneurByType(&temperature, "Lampe", GPIO_PIN_SET); //tafi
			controlActionneurByType(&humSol, "Pompe", GPIO_PIN_SET); //tafi
			State = TX;
			UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
		}
		}else{
			if( !(tempOptimaleN && humOptimaleN && solOptimale)){
										 read_sensor_Data();

										 UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_Control_Actuators), CFG_SEQ_Prio_1);
						}
			else{
						tempOptimaleN =0; humOptimaleN =0; solOptimale=0; lumOptimale= 0;
										             	fanbyHum = 0;
										             	fanbyTemp = 0;
						ventilateur.etat = 0;
						lampe.etat =0;
						pompe.etat = 0;
						controlActionneurByType(&gaz, "Ventilateur", GPIO_PIN_SET); //tafi
						controlActionneurByType(&temperature, "Lampe", GPIO_PIN_SET); //tafi
						controlActionneurByType(&humSol, "Pompe", GPIO_PIN_SET); //tafi
						State = TX;
						UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
					}
		}

}

void gazCallBack(){
	 State = WAIT_FOR_TX;
	 UTIL_SEQ_SetTask((1 << CFG_SEQ_Task_SubGHz_Phy_App_Process), CFG_SEQ_Prio_0);
	 APP_LOG(TS_ON, VLEVEL_L, "Déclenchement du timer du gaz \n\r");
}
/* USER CODE END PrFD */

/* HAL overload functions ---------------------------------------------------------*/

/**
  * @note This function overwrites the __weak one from HAL
  */
HAL_StatusTypeDef HAL_InitTick(uint32_t TickPriority)
{
  /*Don't enable SysTick if TIMER_IF is based on other counters (e.g. RTC) */
  /* USER CODE BEGIN HAL_InitTick_1 */

  /* USER CODE END HAL_InitTick_1 */
  return HAL_OK;
  /* USER CODE BEGIN HAL_InitTick_2 */

  /* USER CODE END HAL_InitTick_2 */
}

/**
  * @note This function overwrites the __weak one from HAL
  */
uint32_t HAL_GetTick(void)
{
  uint32_t ret = 0;
  /* TIMER_IF can be based on other counter the SysTick e.g. RTC */
  /* USER CODE BEGIN HAL_GetTick_1 */

  /* USER CODE END HAL_GetTick_1 */
  if (SYS_TimerInitialisedFlag == 0)
  {
    /* TIMER_IF_GetTimerValue should be used only once UTIL_TIMER_Init() is initialized */
    /* If HAL_Delay or a TIMEOUT countdown is necessary during initialization phase */
    /* please use temporarily another timebase source (SysTick or TIMx), which implies also */
    /* to rework the above function HAL_InitTick() and to call HAL_IncTick() on the timebase IRQ */
    /* Note: when TIMER_IF is based on RTC, stm32wlxx_hal_rtc.c calls this function before TimeServer is functional */
    /* RTC TIMEOUT will not expire, i.e. if RTC has an hw problem it will keep looping in the RTC_Init function */
    /* USER CODE BEGIN HAL_GetTick_EarlyCall */

    /* USER CODE END HAL_GetTick_EarlyCall */
  }
  else
  {
    ret = TIMER_IF_GetTimerValue();
  }
  /* USER CODE BEGIN HAL_GetTick_2 */

  /* USER CODE END HAL_GetTick_2 */
  return ret;
}

/**
  * @note This function overwrites the __weak one from HAL
  */
void HAL_Delay(__IO uint32_t Delay)
{
  /* TIMER_IF can be based on other counter the SysTick e.g. RTC */
  /* USER CODE BEGIN HAL_Delay_1 */

  /* USER CODE END HAL_Delay_1 */
  TIMER_IF_DelayMs(Delay);
  /* USER CODE BEGIN HAL_Delay_2 */

  /* USER CODE END HAL_Delay_2 */
}

/* USER CODE BEGIN Overload_HAL_weaks */

/* USER CODE END Overload_HAL_weaks */
