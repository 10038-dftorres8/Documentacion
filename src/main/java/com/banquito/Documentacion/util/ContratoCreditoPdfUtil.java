package com.banquito.Documentacion.util;

import com.banquito.Documentacion.dto.ContratoCreditoDTO;
import com.banquito.Documentacion.util.dto.PersonaInfoDTO;
import com.banquito.Documentacion.util.dto.VehiculoInfoDTO;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class ContratoCreditoPdfUtil {
    private static String safe(Object value) {
        if (value == null) return "N/A";
        if (value instanceof Number) {
            if (value.toString().equals("0")) return "0";
            return value.toString();
        }
        String str = value.toString();
        if (str.trim().isEmpty()) return "N/A";
        return str;
    }

    public static byte[] generarPdfContrato(ContratoCreditoDTO contrato, PersonaInfoDTO persona, VehiculoInfoDTO vehiculo, String nombreBanco, String direccionBanco, String telefonoBanco, String emailBanco, String representanteBanco, String cedulaRepresentante) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        PdfWriter.getInstance(document, baos);
        document.open();


        BaseColor azulBanco = new BaseColor(30, 60, 120);
        BaseColor grisClaro = new BaseColor(240, 240, 240);
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, azulBanco);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, azulBanco);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, azulBanco);
        Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell cellHeader = new PdfPCell();
        cellHeader.setBorder(Rectangle.NO_BORDER);
        cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph pBanco = new Paragraph("BANCO BANQUITO", fontTitulo);
        pBanco.setAlignment(Element.ALIGN_CENTER);
        cellHeader.addElement(pBanco);
        Paragraph pSucursal = new Paragraph("Sucursal Matriz Quito", fontSubtitulo);
        pSucursal.setAlignment(Element.ALIGN_CENTER);
        cellHeader.addElement(pSucursal);
        Paragraph pDir = new Paragraph("Av. Amazonas N34-99, Edificio Torre Central", fontSmall);
        pDir.setAlignment(Element.ALIGN_CENTER);
        cellHeader.addElement(pDir);
        Paragraph pTel = new Paragraph("Teléfono: 02-2901010 | Email: matriz@banquito.com", fontSmall);
        pTel.setAlignment(Element.ALIGN_CENTER);
        cellHeader.addElement(pTel);
        header.addCell(cellHeader);
        document.add(header);

        document.add(Chunk.NEWLINE);

        Paragraph titulo = new Paragraph("CONTRATO DE CRÉDITO AUTOMOTRIZ N° " + safe(contrato.getNumeroContrato()), fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(5f);
        document.add(titulo);
        document.add(lineaDivisoria());

        Paragraph fechaLugar = new Paragraph("Quito, a 04/08/2025", fontNormal);
        fechaLugar.setAlignment(Element.ALIGN_CENTER);
        fechaLugar.setSpacingAfter(10f);
        document.add(fechaLugar);

        Paragraph intro = new Paragraph("Entre BANCO BANQUITO, en adelante EL BANCO, y la persona cuyos datos constan en este documento, en adelante EL CLIENTE, se celebra el presente contrato de crédito automotriz, sujeto a las siguientes cláusulas:", fontNormal);
        intro.setAlignment(Element.ALIGN_JUSTIFIED);
        intro.setSpacingAfter(12f);
        document.add(intro);

        // Sección: Datos del cliente
        Paragraph datosClienteTitulo = new Paragraph("I. DATOS DEL CLIENTE", fontSubtitulo);
        datosClienteTitulo.setAlignment(Element.ALIGN_LEFT);
        datosClienteTitulo.setSpacingAfter(5f);
        document.add(datosClienteTitulo);
        PdfPTable tablaCliente = new PdfPTable(2);
        tablaCliente.setWidthPercentage(90);
        tablaCliente.setSpacingBefore(2f);
        tablaCliente.setSpacingAfter(10f);
        tablaCliente.addCell(celdaLabel("Nombre completo:", fontLabel));
        tablaCliente.addCell(celdaDato(safe(persona.getNombre()), fontNormal));
        tablaCliente.addCell(celdaLabel("Identificación:", fontLabel));
        tablaCliente.addCell(celdaDato(safe(persona.getTipoIdentificacion()) + " " + safe(persona.getNumeroIdentificacion()), fontNormal));
        tablaCliente.addCell(celdaLabel("Género:", fontLabel));
        tablaCliente.addCell(celdaDato(safe(persona.getGenero()), fontNormal));
        tablaCliente.addCell(celdaLabel("Fecha de nacimiento:", fontLabel));
        tablaCliente.addCell(celdaDato(safe(persona.getFechaNacimiento()), fontNormal));
        tablaCliente.addCell(celdaLabel("Estado civil:", fontLabel));
        tablaCliente.addCell(celdaDato(safe(persona.getEstadoCivil()), fontNormal));
        tablaCliente.addCell(celdaLabel("Nivel de estudio:", fontLabel));
        tablaCliente.addCell(celdaDato(safe(persona.getNivelEstudio()), fontNormal));
        tablaCliente.addCell(celdaLabel("Correo electrónico:", fontLabel));
        tablaCliente.addCell(celdaDato(safe(persona.getCorreoElectronico()), fontNormal));
        document.add(tablaCliente);
        document.add(lineaDivisoria());

        // Sección: Objeto del contrato
        Paragraph objetoTitulo = new Paragraph("II. OBJETO DEL CONTRATO", fontSubtitulo);
        objetoTitulo.setAlignment(Element.ALIGN_LEFT);
        objetoTitulo.setSpacingAfter(5f);
        document.add(objetoTitulo);
        Paragraph objetoTexto = new Paragraph(
            "EL BANCO concede a EL CLIENTE un crédito automotriz por un monto de $" + safe(contrato.getMontoAprobado()) +
            ", a ser pagado en " + safe(contrato.getPlazoFinalMeses()) + " meses, con una tasa de interés anual del " + safe(contrato.getTasaEfectivaAnual()) + "%. El CLIENTE se compromete a destinar el crédito exclusivamente para la adquisición del vehículo detallado en este contrato.",
            fontNormal);
        objetoTexto.setAlignment(Element.ALIGN_JUSTIFIED);
        objetoTexto.setSpacingAfter(10f);
        document.add(objetoTexto);

        // Sección: Condiciones del crédito
        Paragraph condicionesTitulo = new Paragraph("III. CONDICIONES DEL CRÉDITO", fontSubtitulo);
        condicionesTitulo.setAlignment(Element.ALIGN_LEFT);
        condicionesTitulo.setSpacingAfter(5f);
        document.add(condicionesTitulo);
        PdfPTable tablaCredito = new PdfPTable(2);
        tablaCredito.setWidthPercentage(90);
        tablaCredito.setSpacingBefore(2f);
        tablaCredito.setSpacingAfter(10f);
        tablaCredito.addCell(celdaLabel("Monto aprobado:", fontLabel));
        tablaCredito.addCell(celdaDato("$" + safe(contrato.getMontoAprobado()), fontNormal));
        tablaCredito.addCell(celdaLabel("Plazo:", fontLabel));
        tablaCredito.addCell(celdaDato(safe(contrato.getPlazoFinalMeses()) + " meses", fontNormal));
        tablaCredito.addCell(celdaLabel("Tasa de interés anual:", fontLabel));
        tablaCredito.addCell(celdaDato(safe(contrato.getTasaEfectivaAnual()) + "%", fontNormal));
        tablaCredito.addCell(celdaLabel("Fecha de inicio:", fontLabel));
        tablaCredito.addCell(celdaDato((contrato.getFechaGeneracion() != null ? contrato.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"), fontNormal));
        tablaCredito.addCell(celdaLabel("Fecha de finalización:", fontLabel));
        tablaCredito.addCell(celdaDato((contrato.getFechaFirma() != null ? contrato.getFechaFirma().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"), fontNormal));
        document.add(tablaCredito);
        document.add(lineaDivisoria());

        // Sección: Datos del vehículo
        Paragraph datosVehiculoTitulo = new Paragraph("IV. DATOS DEL VEHÍCULO", fontSubtitulo);
        datosVehiculoTitulo.setAlignment(Element.ALIGN_LEFT);
        datosVehiculoTitulo.setSpacingAfter(5f);
        document.add(datosVehiculoTitulo);
        PdfPTable tablaVehiculo = new PdfPTable(2);

        tablaVehiculo.setWidthPercentage(90);
        tablaVehiculo.setSpacingBefore(2f);
        tablaVehiculo.setSpacingAfter(10f);
        tablaVehiculo.addCell(celdaLabel("Marca:", fontLabel));
        tablaVehiculo.addCell(celdaDato(safe(vehiculo.getMarca()), fontNormal));
        tablaVehiculo.addCell(celdaLabel("Modelo:", fontLabel));
        tablaVehiculo.addCell(celdaDato(safe(vehiculo.getModelo()), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Año:", fontLabel));
        // tablaVehiculo.addCell(celdaDato(safe(vehiculo.getAnio()), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Cilindraje:", fontLabel));
        // tablaVehiculo.addCell(celdaDato(safe(vehiculo.getCilindraje()), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Color:", fontLabel));
        // tablaVehiculo.addCell(celdaDato(safe(vehiculo.getColor()), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Placa:", fontLabel));
        // tablaVehiculo.addCell(celdaDato((vehiculo.getIdentificadorVehiculo() != null ? safe(vehiculo.getIdentificadorVehiculo().getPlaca()) : "N/A"), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Chasis:", fontLabel));
        // tablaVehiculo.addCell(celdaDato((vehiculo.getIdentificadorVehiculo() != null ? safe(vehiculo.getIdentificadorVehiculo().getChasis()) : "N/A"), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Motor:", fontLabel));
        // tablaVehiculo.addCell(celdaDato((vehiculo.getIdentificadorVehiculo() != null ? safe(vehiculo.getIdentificadorVehiculo().getMotor()) : "N/A"), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Extras:", fontLabel));
        // tablaVehiculo.addCell(celdaDato(safe(vehiculo.getExtras()), fontNormal));
        // tablaVehiculo.addCell(celdaLabel("Condición:", fontLabel));
        // tablaVehiculo.addCell(celdaDato(safe(vehiculo.getCondicion()), fontNormal));
        document.add(tablaVehiculo);
       // document.add(lineaDivisoria());

        // Sección: Obligaciones
        Paragraph obligacionesTitulo = new Paragraph("V. OBLIGACIONES DEL CLIENTE", fontSubtitulo);
        obligacionesTitulo.setAlignment(Element.ALIGN_LEFT);
        obligacionesTitulo.setSpacingAfter(5f);
        document.add(obligacionesTitulo);
        Paragraph obligacionesTexto = new Paragraph(
            "El CLIENTE se obliga a: (a) Pagar puntualmente las cuotas del crédito; (b) Mantener el vehículo asegurado durante la vigencia del crédito; (c) No vender, ceder o transferir el vehículo sin autorización expresa del BANCO; (d) Cumplir con todas las obligaciones legales y contractuales derivadas del presente contrato.",
            fontNormal);
        obligacionesTexto.setAlignment(Element.ALIGN_JUSTIFIED);
        obligacionesTexto.setSpacingAfter(10f);
        document.add(obligacionesTexto);

        // Sección: Garantías
        Paragraph garantiasTitulo = new Paragraph("VI. GARANTÍAS", fontSubtitulo);
        garantiasTitulo.setAlignment(Element.ALIGN_LEFT);
        garantiasTitulo.setSpacingAfter(5f);
        document.add(garantiasTitulo);
        Paragraph garantiasTexto = new Paragraph(
            "El vehículo adquirido con el presente crédito constituye la garantía principal a favor del BANCO, quien podrá ejercer los derechos que la ley le confiere en caso de incumplimiento por parte del CLIENTE.",
            fontNormal);
        garantiasTexto.setAlignment(Element.ALIGN_JUSTIFIED);
        garantiasTexto.setSpacingAfter(10f);
        document.add(garantiasTexto);

        // Sección: Incumplimiento
        Paragraph incumplimientoTitulo = new Paragraph("VII. INCUMPLIMIENTO Y MORA", fontSubtitulo);
        incumplimientoTitulo.setAlignment(Element.ALIGN_LEFT);
        incumplimientoTitulo.setSpacingAfter(5f);
        document.add(incumplimientoTitulo);
        Paragraph incumplimientoTexto = new Paragraph(
            "En caso de mora o incumplimiento de las obligaciones contractuales, el BANCO podrá declarar vencido anticipadamente el crédito, exigir el pago inmediato de la totalidad de la deuda y ejecutar la garantía, aplicando los intereses y penalidades establecidos por la ley.",
            fontNormal);
        incumplimientoTexto.setAlignment(Element.ALIGN_JUSTIFIED);
        incumplimientoTexto.setSpacingAfter(10f);
        document.add(incumplimientoTexto);

        // Línea divisoria
        document.add(lineaDivisoria());

        // Sección: Firmas
        Paragraph firmasTitulo = new Paragraph("VIII. FIRMAS", fontSubtitulo);
        firmasTitulo.setAlignment(Element.ALIGN_LEFT);
        firmasTitulo.setSpacingAfter(10f);
        document.add(firmasTitulo);
        PdfPTable tablaFirmas = new PdfPTable(2);
        tablaFirmas.setWidthPercentage(90);
        tablaFirmas.setSpacingBefore(20f);
        tablaFirmas.setWidths(new int[]{1, 1});
        PdfPCell celdaBanco = new PdfPCell();
        celdaBanco.setBorder(Rectangle.NO_BORDER);
        celdaBanco.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaBanco.addElement(new Paragraph("_________________________", fontNormal));
        celdaBanco.addElement(new Paragraph("Representante del Banco", fontSmall));
        celdaBanco.addElement(new Paragraph("Nombre: " + safe(representanteBanco), fontSmall));
        celdaBanco.addElement(new Paragraph("C.I.: " + safe(cedulaRepresentante), fontSmall));
        tablaFirmas.addCell(celdaBanco);
        PdfPCell celdaCliente = new PdfPCell();
        celdaCliente.setBorder(Rectangle.NO_BORDER);
        celdaCliente.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaCliente.addElement(new Paragraph("_________________________", fontNormal));
        celdaCliente.addElement(new Paragraph("Cliente", fontSmall));
        celdaCliente.addElement(new Paragraph("Nombre: " + safe(persona.getNombre()), fontSmall));
        celdaCliente.addElement(new Paragraph("C.I.: " + safe(persona.getNumeroIdentificacion()), fontSmall));
        tablaFirmas.addCell(celdaCliente);
        document.add(tablaFirmas);

        document.close();
        return baos.toByteArray();
    }

    private static PdfPCell celdaLabel(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(new BaseColor(230, 230, 250));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5f);
        return cell;
    }

    private static PdfPCell celdaDato(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(BaseColor.WHITE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5f);
        return cell;
    }

    private static LineSeparator lineaDivisoria() {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(new BaseColor(180, 180, 180));
        ls.setLineWidth(1f);
        return ls;
    }
    }
