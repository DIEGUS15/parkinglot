package com.parkingLot.utils;

import com.parkingLot.exceptions.BadRequestException;

import java.util.regex.Pattern;

public class PlacaValidator {

    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");

    public static void validarPlaca(String placa) {
        if (placa == null || placa.isEmpty()) {
            throw new BadRequestException("La placa no puede estar vacía");
        }

        if (placa.length() != 6) {
            throw new BadRequestException(
                    "La placa debe tener exactamente 6 caracteres. Placa recibida: '" + placa + "' (" + placa.length()
                            + " caracteres)");
        }

        if (!PLACA_PATTERN.matcher(placa).matches()) {
            throw new BadRequestException(
                    "La placa solo puede contener caracteres alfanuméricos (A-Z, 0-9). No se permiten caracteres especiales ni la letra ñ");
        }
    }

    public static String normalizarYValidarPlaca(String placa) {
        if (placa == null) {
            throw new BadRequestException("La placa no puede estar vacía");
        }

        String placaNormalizada = placa.trim().toUpperCase();

        validarPlaca(placaNormalizada);

        return placaNormalizada;
    }
}
