package com.banquito.Documentacion.util;

import com.banquito.Documentacion.dto.PagareDTO;
import com.banquito.Documentacion.util.dto.PersonaInfoDTO;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class PagareCreditoPdfUtil {
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

    public static byte[] generarPdfPagare(PagareDTO pagare, PersonaInfoDTO deudor, String nombreBanco, String direccionBanco, String telefonoBanco, String emailBanco, String representanteBanco, String cedulaRepresentante) throws Exception {
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

        Paragraph titulo = new Paragraph("PAGARÉ N° " + safe(pagare.getNumeroCuota()), fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(5f);
        document.add(titulo);
        document.add(lineaDivisoria());

        Paragraph fechaLugar = new Paragraph("Quito, a " + (pagare.getFechaVencimiento() != null ? pagare.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"), fontNormal);
        fechaLugar.setAlignment(Element.ALIGN_CENTER);
        fechaLugar.setSpacingAfter(10f);
        document.add(fechaLugar);


        Paragraph intro = new Paragraph(
            "Por el presente documento, yo, " + safe(deudor.getNombre()) + " (" + safe(deudor.getTipoIdentificacion()) + ": " + safe(deudor.getNumeroIdentificacion()) + "), me obligo a pagar incondicionalmente a la orden de " + safe(nombreBanco) + ", la suma de: $" + safe(pagare.getMontoCuota()) + " (" + safe(pagare.getMontoCuota()) + " dólares de los Estados Unidos de América), en la ciudad de Quito, en la fecha de vencimiento indicada, correspondiente a la cuota N° " + safe(pagare.getNumeroCuota()) + " del contrato de crédito automotriz.",
            fontNormal);
        intro.setAlignment(Element.ALIGN_JUSTIFIED);
        intro.setSpacingAfter(12f);
        document.add(intro);


        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidthPercentage(90);
        tablaDatos.setSpacingBefore(2f);
        tablaDatos.setSpacingAfter(10f);
        tablaDatos.addCell(celdaLabel("Deudor:", fontLabel));
        tablaDatos.addCell(celdaDato(safe(deudor.getNombre()), fontNormal));
        tablaDatos.addCell(celdaLabel("Identificación:", fontLabel));
        tablaDatos.addCell(celdaDato(safe(deudor.getTipoIdentificacion()) + " " + safe(deudor.getNumeroIdentificacion()), fontNormal));
        tablaDatos.addCell(celdaLabel("Monto a pagar:", fontLabel));
        tablaDatos.addCell(celdaDato("$" + safe(pagare.getMontoCuota()), fontNormal));
        tablaDatos.addCell(celdaLabel("Fecha de vencimiento:", fontLabel));
        tablaDatos.addCell(celdaDato((pagare.getFechaVencimiento() != null ? pagare.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"), fontNormal));
        tablaDatos.addCell(celdaLabel("Número de cuota:", fontLabel));
        tablaDatos.addCell(celdaDato(safe(pagare.getNumeroCuota()), fontNormal));
        document.add(tablaDatos);
        document.add(lineaDivisoria());

        Paragraph clausula = new Paragraph(
            "En caso de incumplimiento en el pago de la presente obligación, el DEUDOR acepta expresamente que el BANCO podrá iniciar las acciones legales correspondientes para el cobro de la deuda, incluyendo intereses de mora y gastos judiciales, conforme a la legislación vigente.",
            fontNormal);
        clausula.setAlignment(Element.ALIGN_JUSTIFIED);
        clausula.setSpacingAfter(20f);
        document.add(clausula);

       
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
        PdfPCell celdaDeudor = new PdfPCell();
        celdaDeudor.setBorder(Rectangle.NO_BORDER);
        celdaDeudor.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaDeudor.addElement(new Paragraph("_________________________", fontNormal));
        celdaDeudor.addElement(new Paragraph("Deudor", fontSmall));
        celdaDeudor.addElement(new Paragraph("Nombre: " + safe(deudor.getNombre()), fontSmall));
        celdaDeudor.addElement(new Paragraph("C.I.: " + safe(deudor.getNumeroIdentificacion()), fontSmall));
        tablaFirmas.addCell(celdaDeudor);
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
