package com.banquito.Documentacion.util;

import com.banquito.Documentacion.dto.ContratoCompraVentaDTO;
import com.banquito.Documentacion.util.dto.PersonaInfoDTO;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class ContratoCompraVentaPdfUtil {
    
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

    public static byte[] generarPdfContratoCompraVenta(
            ContratoCompraVentaDTO contrato, 
            PersonaInfoDTO comprador, 
            String nombreBanco, 
            String direccionBanco, 
            String telefonoBanco, 
            String emailBanco, 
            String representanteBanco, 
            String cedulaRepresentante) throws Exception {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        PdfWriter.getInstance(document, baos);
        document.open();

        BaseColor azulBanco = new BaseColor(30, 60, 120);
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, azulBanco);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, azulBanco);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, azulBanco);
        Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

        // Header del banco
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

        // Título del contrato
        Paragraph titulo = new Paragraph("CONTRATO DE COMPRA-VENTA DE VEHÍCULO", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(5f);
        document.add(titulo);
        
        Paragraph numeroContrato = new Paragraph("N° " + safe(contrato.getNumeroContrato()), fontSubtitulo);
        numeroContrato.setAlignment(Element.ALIGN_CENTER);
        numeroContrato.setSpacingAfter(10f);
        document.add(numeroContrato);
        
        document.add(lineaDivisoria());

        // Fecha y lugar
        Paragraph fechaLugar = new Paragraph("Quito, a " + (contrato.getFechaGeneracion() != null ? 
                contrato.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"), fontNormal);
        fechaLugar.setAlignment(Element.ALIGN_CENTER);
        fechaLugar.setSpacingAfter(15f);
        document.add(fechaLugar);

        // Introducción del contrato
        Paragraph intro = new Paragraph(
            "Entre " + safe(nombreBanco) + ", representado por " + safe(representanteBanco) + 
            " (" + safe(cedulaRepresentante) + "), en adelante denominado \"EL VENDEDOR\", y " + 
            safe(comprador.getNombre()) + " (" + safe(comprador.getTipoIdentificacion()) + ": " + 
            safe(comprador.getNumeroIdentificacion()) + "), en adelante denominado \"EL COMPRADOR\", " +
            "se celebra el presente contrato de compra-venta de vehículo bajo los siguientes términos y condiciones:",
            fontNormal);
        intro.setAlignment(Element.ALIGN_JUSTIFIED);
        intro.setSpacingAfter(15f);
        document.add(intro);

        // Tabla de datos del contrato
        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidthPercentage(90);
        tablaDatos.setSpacingBefore(5f);
        tablaDatos.setSpacingAfter(15f);
        
        tablaDatos.addCell(celdaLabel("Número de Contrato:", fontLabel));
        tablaDatos.addCell(celdaDato(safe(contrato.getNumeroContrato()), fontNormal));
        
        tablaDatos.addCell(celdaLabel("Comprador:", fontLabel));
        tablaDatos.addCell(celdaDato(safe(comprador.getNombre()), fontNormal));
        
        tablaDatos.addCell(celdaLabel("Identificación:", fontLabel));
        tablaDatos.addCell(celdaDato(safe(comprador.getTipoIdentificacion()) + " " + safe(comprador.getNumeroIdentificacion()), fontNormal));
        
        tablaDatos.addCell(celdaLabel("Precio Final del Vehículo:", fontLabel));
        tablaDatos.addCell(celdaDato("$" + safe(contrato.getPrecioFinalVehiculo()), fontNormal));
        
        tablaDatos.addCell(celdaLabel("Estado del Contrato:", fontLabel));
        tablaDatos.addCell(celdaDato(safe(contrato.getEstado()), fontNormal));
        
        tablaDatos.addCell(celdaLabel("Fecha de Generación:", fontLabel));
        tablaDatos.addCell(celdaDato((contrato.getFechaGeneracion() != null ? 
                contrato.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"), fontNormal));
        
        document.add(tablaDatos);
        document.add(lineaDivisoria());

        // Cláusulas del contrato
        Paragraph clausula1 = new Paragraph(
            "PRIMERA.- OBJETO DEL CONTRATO: EL VENDEDOR se compromete a transferir la propiedad del vehículo " +
            "descrito en los anexos del presente contrato a EL COMPRADOR, quien se obliga a pagar el precio convenido.",
            fontNormal);
        clausula1.setAlignment(Element.ALIGN_JUSTIFIED);
        clausula1.setSpacingAfter(10f);
        document.add(clausula1);

        Paragraph clausula2 = new Paragraph(
            "SEGUNDA.- PRECIO: El precio total del vehículo es de $" + safe(contrato.getPrecioFinalVehiculo()) + 
            " (dólares de los Estados Unidos de América), el cual será cancelado según los términos acordados " +
            "en el financiamiento vehicular correspondiente.",
            fontNormal);
        clausula2.setAlignment(Element.ALIGN_JUSTIFIED);
        clausula2.setSpacingAfter(10f);
        document.add(clausula2);

        Paragraph clausula3 = new Paragraph(
            "TERCERA.- ENTREGA: EL VENDEDOR se compromete a entregar el vehículo en las condiciones pactadas " +
            "una vez cumplidos todos los requisitos legales y financieros establecidos.",
            fontNormal);
        clausula3.setAlignment(Element.ALIGN_JUSTIFIED);
        clausula3.setSpacingAfter(20f);
        document.add(clausula3);

        // Tabla de firmas
        PdfPTable tablaFirmas = new PdfPTable(2);
        tablaFirmas.setWidthPercentage(90);
        tablaFirmas.setSpacingBefore(30f);
        tablaFirmas.setWidths(new int[]{1, 1});
        
        PdfPCell celdaVendedor = new PdfPCell();
        celdaVendedor.setBorder(Rectangle.NO_BORDER);
        celdaVendedor.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaVendedor.addElement(new Paragraph("_________________________", fontNormal));
        celdaVendedor.addElement(new Paragraph("EL VENDEDOR", fontSmall));
        celdaVendedor.addElement(new Paragraph("Nombre: " + safe(representanteBanco), fontSmall));
        celdaVendedor.addElement(new Paragraph("C.I.: " + safe(cedulaRepresentante), fontSmall));
        celdaVendedor.addElement(new Paragraph(safe(nombreBanco), fontSmall));
        tablaFirmas.addCell(celdaVendedor);
        
        PdfPCell celdaComprador = new PdfPCell();
        celdaComprador.setBorder(Rectangle.NO_BORDER);
        celdaComprador.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaComprador.addElement(new Paragraph("_________________________", fontNormal));
        celdaComprador.addElement(new Paragraph("EL COMPRADOR", fontSmall));
        celdaComprador.addElement(new Paragraph("Nombre: " + safe(comprador.getNombre()), fontSmall));
        celdaComprador.addElement(new Paragraph("C.I.: " + safe(comprador.getNumeroIdentificacion()), fontSmall));
        tablaFirmas.addCell(celdaComprador);
        
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
