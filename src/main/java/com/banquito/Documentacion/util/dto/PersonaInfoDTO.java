package com.banquito.Documentacion.util.dto;

import lombok.Data;

@Data
public class PersonaInfoDTO {
    private String id;
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String nombre;
    private String genero;
    private String fechaNacimiento;
    private String estadoCivil;
    private String nivelEstudio;
    private String correoElectronico;
    private String estado;
}
