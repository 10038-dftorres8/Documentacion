package com.banquito.Documentacion.util;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.banquito.Documentacion.dto.DetalleSolicitudResponseDTO;


public class ContratoCreditoPdfUtil {
    private static String safe(Object value) {
        if (value == null) return "";
        if (value instanceof Number) {
            return value.toString();
        }
        String str = value.toString();
        if (str.trim().isEmpty()) return "";
        return str;
    }

    /**
     * Genera el PDF del contrato usando solo los datos del JSON proporcionado por el FeignClient.
     * @param datosSolicitud objeto con los campos del JSON de la solicitud
     * @return PDF en bytes
     */
    public static byte[] generarPdfContratoSoloDatosJson(DetalleSolicitudResponseDTO datosSolicitud) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        PdfWriter.getInstance(document, baos);
        document.open();

    BaseColor azulBanco = new BaseColor(18, 44, 90);
    BaseColor azulClaro = new BaseColor(44, 130, 201);
    BaseColor grisClaro = new BaseColor(245, 247, 250);
    BaseColor grisOscuro = new BaseColor(80, 80, 80);
    BaseColor celeste = new BaseColor(220, 230, 250);
    BaseColor blanco = BaseColor.WHITE;
    Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, blanco);
    Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, azulClaro);
    Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, grisOscuro);
    Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, azulBanco);
    Font fontDato = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
    Font fontLegal = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, grisOscuro);
    Font fontPie = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, azulBanco);
    Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

    // Header con fondo azul y texto blanco
    PdfPTable header = new PdfPTable(1);
    header.setWidthPercentage(100);
    PdfPCell cellHeader = new PdfPCell();
    cellHeader.setBackgroundColor(azulBanco);
    cellHeader.setBorder(Rectangle.NO_BORDER);
    cellHeader.setPaddingTop(18f);
    cellHeader.setPaddingBottom(18f);
    cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
    Paragraph pBanco = new Paragraph("BANCO BANQUITO", fontTitulo);
    pBanco.setAlignment(Element.ALIGN_CENTER);
    cellHeader.addElement(pBanco);
    Paragraph pSucursal = new Paragraph("Sucursal Matriz Quito", fontSubtitulo);
    pSucursal.setAlignment(Element.ALIGN_CENTER);
    pSucursal.getFont().setColor(blanco);
    cellHeader.addElement(pSucursal);
    header.addCell(cellHeader);
    document.add(header);
    document.add(Chunk.NEWLINE);

        // Título y número de solicitud
    // Título con barra lateral azul
    PdfPTable tituloTable = new PdfPTable(new float[]{0.03f, 0.97f});
    tituloTable.setWidthPercentage(100);
    PdfPCell barra = new PdfPCell();
    barra.setBackgroundColor(azulClaro);
    barra.setBorder(Rectangle.NO_BORDER);
    barra.setFixedHeight(40f);
    tituloTable.addCell(barra);
    PdfPCell tituloCell = new PdfPCell(new Phrase("CONTRATO DE CRÉDITO AUTOMOTRIZ", fontSubtitulo));
    tituloCell.setBackgroundColor(grisClaro);
    tituloCell.setBorder(Rectangle.NO_BORDER);
    tituloCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    tituloCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    tituloCell.setPaddingLeft(18f);
    tituloCell.setPaddingTop(10f);
    tituloCell.setPaddingBottom(10f);
    tituloTable.addCell(tituloCell);
    document.add(tituloTable);
    Paragraph subtitulo = new Paragraph("N° " + safe(datosSolicitud.getNumeroSolicitud()), fontLabel);
    subtitulo.setAlignment(Element.ALIGN_RIGHT);
    subtitulo.setSpacingAfter(10f);
    document.add(subtitulo);
    document.add(lineaDivisoria());

    // Fecha y lugar
    String fecha = "";
    Paragraph fechaLugar = new Paragraph("Quito, a " + fecha, fontLabel);
    fechaLugar.setAlignment(Element.ALIGN_RIGHT);
    fechaLugar.setSpacingAfter(10f);
    document.add(fechaLugar);

        // Introducción legal con cita en cursiva
        Paragraph intro = new Paragraph(
            "Entre BANCO BANQUITO, en adelante EL BANCO, y la persona cuyos datos constan en este documento, en adelante EL CLIENTE, se celebra el presente contrato de crédito automotriz, sujeto a las siguientes cláusulas:",
            fontLegal
        );
        intro.setAlignment(Element.ALIGN_JUSTIFIED);
        intro.setFirstLineIndent(30f);
        intro.setSpacingAfter(8f);
        document.add(intro);
        Paragraph intro2 = new Paragraph(
            "\"EL BANCO concede a EL CLIENTE un crédito automotriz por un monto de $25,000.00, a ser pagado en 36 meses, con una tasa de interés anual del 10.25%. El CLIENTE se compromete a destinar el crédito exclusivamente para la adquisición del vehículo detallado en este contrato.\"",
            fontLegal
        );
        intro2.setAlignment(Element.ALIGN_JUSTIFIED);
        intro2.setFirstLineIndent(30f);
        intro2.setSpacingAfter(14f);
        document.add(intro2);

        // I. DATOS DEL CLIENTE
        document.add(seccionTitulo("I. DATOS DEL CLIENTE", azulClaro));
        PdfPTable tablaCliente = tablaDatos2(
            new String[]{"Nombre completo:", safe(datosSolicitud.getNombresSolicitante()), "Cédula:", safe(datosSolicitud.getCedulaSolicitante())},
            fontLabel, fontDato, blanco, grisClaro
        );
        document.add(tablaCliente);
        document.add(lineaDivisoriaDoble());

        // II. DATOS DEL VEHÍCULO
        document.add(seccionTitulo("II. DATOS DEL VEHÍCULO", azulClaro));
        PdfPTable tablaVehiculo = tablaDatos2(
            new String[]{
                "Placa:", safe(datosSolicitud.getPlacaVehiculo()),
                "Marca:", safe(datosSolicitud.getMarcaVehiculo()),
                "Modelo:", safe(datosSolicitud.getModeloVehiculo()),
                "Año:", safe(datosSolicitud.getAnioVehiculo()),
                "Valor Vehículo:", "$" + safe(datosSolicitud.getValorVehiculo())
            },
            fontLabel, fontDato, blanco, grisClaro
        );
        document.add(tablaVehiculo);
        document.add(lineaDivisoriaDoble());

        // III. DATOS DEL CONCESIONARIO
        document.add(seccionTitulo("III. DATOS DEL CONCESIONARIO", azulClaro));
        PdfPTable tablaConcesionario = tablaDatos2(
            new String[]{
                "RUC:", safe(datosSolicitud.getRucConcesionario()),
                "Razón Social:", safe(datosSolicitud.getRazonSocialConcesionario()),
                "Dirección:", safe(datosSolicitud.getDireccionConcesionario())
            },
            fontLabel, fontDato, blanco, grisClaro
        );
        document.add(tablaConcesionario);
        document.add(lineaDivisoriaDoble());

        // IV. DATOS DEL VENDEDOR
        document.add(seccionTitulo("IV. DATOS DEL VENDEDOR", azulClaro));
        PdfPTable tablaVendedor = tablaDatos2(
            new String[]{
                "Cédula:", safe(datosSolicitud.getCedulaVendedor()),
                "Nombre:", safe(datosSolicitud.getNombreVendedor()),
                "Teléfono:", safe(datosSolicitud.getTelefonoVendedor()),
                "Email:", safe(datosSolicitud.getEmailVendedor())
            },
            fontLabel, fontDato, blanco, grisClaro
        );
    document.add(tablaVendedor);
    // Espacio mínimo después de datos del vendedor
    Paragraph espacioMinimo = new Paragraph();
    espacioMinimo.setSpacingAfter(2f);
    document.add(espacioMinimo);
    document.add(lineaDivisoriaDoble());

        // V. DATOS DEL PRÉSTAMO
        document.add(seccionTitulo("V. DATOS DEL PRÉSTAMO", azulClaro));
        PdfPTable tablaPrestamo = tablaDatos2(
            new String[]{
                "Nombre Préstamo:", safe(datosSolicitud.getIdPrestamo()),
                "Monto Solicitado:", "$" + safe(datosSolicitud.getMontoSolicitado()),
                "Plazo (meses):", safe(datosSolicitud.getPlazoMeses())
            },
            fontLabel, fontDato, blanco, grisClaro
        );
        document.add(tablaPrestamo);
        document.add(lineaDivisoriaDoble());
    // Pie de página legal
    document.add(Chunk.NEWLINE);
    Paragraph pie = new Paragraph("Este contrato es generado electrónicamente por BANCO BANQUITO. Para consultas: www.banquito.com.ec | 1800-BANQUITO", fontPie);
    pie.setAlignment(Element.ALIGN_CENTER);
    pie.setSpacingBefore(20f);
    document.add(pie);

        // VI. OBLIGACIONES DEL CLIENTE
        document.add(seccionTitulo("VI. OBLIGACIONES DEL CLIENTE", azulBanco));
        Paragraph obligaciones = new Paragraph(
            "El CLIENTE se obliga a: (a) Pagar puntualmente las cuotas del crédito; (b) Mantener el vehículo asegurado durante la vigencia del crédito; (c) No vender, ceder o transferir el vehículo sin autorización expresa del BANCO; (d) Cumplir con todas las obligaciones legales y contractuales derivadas del presente contrato.",
            fontNormal
        );
        obligaciones.setAlignment(Element.ALIGN_JUSTIFIED);
        obligaciones.setFirstLineIndent(30f);
        obligaciones.setSpacingAfter(10f);
        document.add(obligaciones);
        document.add(lineaDivisoria());

        // VII. GARANTÍAS
        document.add(seccionTitulo("VII. GARANTÍAS", azulBanco));
        Paragraph garantias = new Paragraph(
            "El vehículo adquirido con el presente crédito constituye la garantía principal a favor del BANCO, quien podrá ejercer los derechos que la ley le confiere en caso de incumplimiento por parte del CLIENTE.",
            fontNormal
        );
        garantias.setAlignment(Element.ALIGN_JUSTIFIED);
        garantias.setFirstLineIndent(30f);
        garantias.setSpacingAfter(10f);
        document.add(garantias);
        document.add(lineaDivisoria());

        // VIII. INCUMPLIMIENTO Y MORA
        document.add(seccionTitulo("VIII. INCUMPLIMIENTO Y MORA", azulBanco));
        Paragraph mora = new Paragraph(
            "En caso de mora o incumplimiento de las obligaciones contractuales, el BANCO podrá declarar vencido anticipadamente el crédito, exigir el pago inmediato de la totalidad de la deuda y ejecutar la garantía, aplicando los intereses y penalidades establecidos por la ley.",
            fontNormal
        );
        mora.setAlignment(Element.ALIGN_JUSTIFIED);
        mora.setFirstLineIndent(30f);
        mora.setSpacingAfter(10f);
        document.add(mora);
        document.add(lineaDivisoria());

    // IX. FIRMAS
    // Espacio mínimo antes de firmas
    Paragraph espacioAntesFirmas = new Paragraph();
    espacioAntesFirmas.setSpacingAfter(2f);
    document.add(espacioAntesFirmas);
    document.add(seccionTitulo("IX. FIRMAS", azulBanco));
        PdfPTable tablaFirmas = new PdfPTable(2);
        tablaFirmas.setWidthPercentage(100);
        tablaFirmas.setSpacingBefore(20f);
        tablaFirmas.setWidths(new int[]{1, 1});
        PdfPCell celdaBanco = new PdfPCell();
        celdaBanco.setBorder(Rectangle.NO_BORDER);
        celdaBanco.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaBanco.setPaddingTop(30f);
        celdaBanco.addElement(new Paragraph("_________________________", fontNormal));
        celdaBanco.addElement(new Paragraph("Representante del Banco", fontSmall));
        celdaBanco.addElement(new Paragraph("Nombre: Lic. Juan Alejandro Gómez Vélez", fontSmall));
        celdaBanco.addElement(new Paragraph("C.I.: 0158693578", fontSmall));
        tablaFirmas.addCell(celdaBanco);
        PdfPCell celdaCliente = new PdfPCell();
        celdaCliente.setBorder(Rectangle.NO_BORDER);
        celdaCliente.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaCliente.setPaddingTop(30f);
        celdaCliente.addElement(new Paragraph("_________________________", fontNormal));
        celdaCliente.addElement(new Paragraph("Cliente", fontSmall));
        celdaCliente.addElement(new Paragraph("Nombre: " + safe(datosSolicitud.getNombresSolicitante()), fontSmall));
        celdaCliente.addElement(new Paragraph("C.I.: " + safe(datosSolicitud.getCedulaSolicitante()), fontSmall));
        tablaFirmas.addCell(celdaCliente);
        document.add(tablaFirmas);

        document.close();
        return baos.toByteArray();
    }


    // Celdas con color y borde para tablas profesionales
    private static PdfPCell celdaLabel(String texto, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new BaseColor(180, 180, 180));
        cell.setPadding(7f);
        return cell;
    }

    private static PdfPCell celdaDato(String texto, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new BaseColor(180, 180, 180));
        cell.setPadding(7f);
        return cell;
    }

    // Tabla de datos con filas alternas y bordes redondeados
    private static PdfPTable tablaDatos2(String[] datos, Font fontLabel, Font fontDato, BaseColor bg1, BaseColor bg2) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        for (int i = 0; i < datos.length; i += 2) {
            BaseColor bg = (i/2) % 2 == 0 ? bg1 : bg2;
            PdfPCell label = celdaLabel(datos[i], fontLabel, bg);
            label.setBorderWidthLeft(2f);
            label.setBorderColorLeft(new BaseColor(44, 130, 201));
            tabla.addCell(label);
            PdfPCell dato = celdaDato(datos[i+1], fontDato, BaseColor.WHITE);
            dato.setBorderWidthRight(2f);
            dato.setBorderColorRight(new BaseColor(44, 130, 201));
            tabla.addCell(dato);
        }
        return tabla;
    }

    // Título de sección con barra lateral y fondo
    private static Paragraph seccionTitulo(String texto, BaseColor color) {
    Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, color);
    Paragraph p = new Paragraph(texto, font);
    p.setSpacingBefore(8f); 
    p.setSpacingAfter(2f);  
    p.setAlignment(Element.ALIGN_LEFT);
    return p;
    }

    // Separador doble
    private static LineSeparator lineaDivisoriaDoble() {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(new BaseColor(44, 130, 201));
        ls.setLineWidth(2f);
        return ls;
    }

    private static LineSeparator lineaDivisoria() {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(new BaseColor(180, 180, 180));
        ls.setLineWidth(1f);
        return ls;
    }
}


