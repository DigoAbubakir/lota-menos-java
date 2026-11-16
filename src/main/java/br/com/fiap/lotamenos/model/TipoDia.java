package br.com.fiap.lotamenos.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Tipo de dia usado no histórico de lotação.
 */
public enum TipoDia {
    UTIL,
    SABADO,
    DOMINGO;

    /** Segunda a sexta = UTIL, sábado = SABADO, domingo = DOMINGO. */
    public static TipoDia de(LocalDate data) {
        DayOfWeek dia = data.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY) {
            return SABADO;
        }
        if (dia == DayOfWeek.SUNDAY) {
            return DOMINGO;
        }
        return UTIL;
    }
}
