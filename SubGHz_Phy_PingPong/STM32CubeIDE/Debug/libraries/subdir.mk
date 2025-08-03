################################################################################
# Automatically-generated file. Do not edit!
# Toolchain: GNU Tools for STM32 (13.3.rel1)
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../libraries/BH1750.c \
../libraries/bme680.c \
../libraries/delays.c 

OBJS += \
./libraries/BH1750.o \
./libraries/bme680.o \
./libraries/delays.o 

C_DEPS += \
./libraries/BH1750.d \
./libraries/bme680.d \
./libraries/delays.d 


# Each subdirectory must supply rules for building sources it contributes
libraries/%.o libraries/%.su libraries/%.cyclo: ../libraries/%.c libraries/subdir.mk
	arm-none-eabi-gcc "$<" -mcpu=cortex-m4 -std=gnu11 -g3 -DDEBUG -DCORE_CM4 -DUSE_HAL_DRIVER -DSTM32WL55xx -c -I../../Core/Inc -I../../SubGHz_Phy/App -I../../SubGHz_Phy/Target -I../../../../../../../Utilities/trace/adv_trace -I../../../../../../../Drivers/STM32WLxx_HAL_Driver/Inc -I../../../../../../../Drivers/STM32WLxx_HAL_Driver/Inc/Legacy -I../../../../../../../Utilities/misc -I../../../../../../../Utilities/sequencer -I../../../../../../../Utilities/timer -I../../../../../../../Utilities/lpm/tiny_lpm -I../../../../../../../Drivers/CMSIS/Device/ST/STM32WLxx/Include -I../../../../../../../Middlewares/Third_Party/SubGHz_Phy -I../../../../../../../Middlewares/Third_Party/SubGHz_Phy/stm32_radio_driver -I../../../../../../../Drivers/CMSIS/Include -I../../../../../../../Drivers/BSP/STM32WLxx_Nucleo -I"C:/Users/Eya Bessaoud/OneDrive/Documents/IOTIneternship/Agri_code_lora-e5-main/Agri_code_lora-e5-main/Projects/NUCLEO-WL55JC/Applications/SubGHz_Phy/SubGHz_Phy_PingPong/STM32CubeIDE/libraries" -Og -ffunction-sections -fdata-sections -Wall -fstack-usage -fcyclomatic-complexity -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" --specs=nano.specs -mfloat-abi=soft -mthumb -o "$@"

clean: clean-libraries

clean-libraries:
	-$(RM) ./libraries/BH1750.cyclo ./libraries/BH1750.d ./libraries/BH1750.o ./libraries/BH1750.su ./libraries/bme680.cyclo ./libraries/bme680.d ./libraries/bme680.o ./libraries/bme680.su ./libraries/delays.cyclo ./libraries/delays.d ./libraries/delays.o ./libraries/delays.su

.PHONY: clean-libraries

