/*  PDFBook - a simple application to generate a booklet from of a PDF.
 *
 *  Copyright 2020 Philip Lockett.
 *
 *  This file is part of PDFBook.
 *
 *  PDFBook is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PDFBook is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PDFBook.  If not, see <https://www.gnu.org/licenses/>.
 */

 /*
 * As a standalone file, PDFBook is a simple application to generate a booklet
 * from of a source PDF document. It requires 2 parameters, the source PDF and
 * the name of the new PDF. However, it can be used as a java class, in which
 * case PDFBook.main() should be superseded.
 *
 * Example usage:
 *  java -jar path-to-PDFBook.jar path-to-source.pdf path-to-new.pdf
 *
 * Dependencies:
 *  PDFbox (pdfbox-app-2.0.19.jar)
 *  https://pdfbox.apache.org/download.cgi
 *
 * This code supports multi-sheet sections. For more information on bookbinding
 * terms and techniques refer to:
 *  https://en.wikipedia.org/wiki/Bookbinding#Terms_and_techniques
 *  https://www.formaxprinting.com/blog/2016/11/
 *      booklet-layout-how-to-arrange-the-pages-of-a-saddle-stitched-booklet/
 *  https://www.studentbookbinding.co.uk/blog/
 *      how-to-set-up-pagination-section-sewn-bindings
 *
 * The document is processed in groups of 4 pages for each sheet of paper, where
 * each page is captured as a BufferedImage. The 4th page is rotated anti-
 * clockwise and scaled to fit on the bottom half of one side of the sheet. The
 * 1st page is rotated anti-clockwise and scaled to fit on the top half of the
 * same side of the sheet. On the reverse side, the 2nd page is rotated
 * clockwise and scaled to fit on the top half and the 3rd page is rotated
 * clockwise and scaled to fit on the bottom half. This process is repeated for
 * all groups of 4 pages in the source document.
 */
package com.phillockett65;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

/**
 *
 * @author Phil
 */
public class PDFBook {

    private PDRectangle PS = PDRectangle.LETTER;
    private int sheetCount = 1;
    private int firstPage = 0;
    private int lastPage = 0;
    private boolean rotate = true;      // Required?

    private final String sourcePDF;     // The source PDF filepath.
    private final String outputPDF;     // The generated PDF filepath.
    private int MAX = 0;

    private PDDocument inputDoc;        // The source PDF document.
    private PDDocument outputDoc;       // The generated PDF document.
    private PDPage page;                // Current page of "outputDoc".



    /**
     * Constructor.
     *
     * @param inPDF file path for source PDF.
     * @param outPDF file path for generated PDF.
     */
    public PDFBook(String inPDF, String outPDF) {
        sourcePDF = inPDF;
        outputPDF = outPDF;

        try {
            inputDoc = PDDocument.load(new File(sourcePDF));
            MAX = inputDoc.getNumberOfPages();
            lastPage = MAX;

            if (inputDoc != null) {
                inputDoc.close();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * System entry point for stand alone, command line version.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            PDFBook booklet = new PDFBook(args[0], args[1]);
            booklet.setPageSize(PDRectangle.LETTER);
            booklet.setRotate(true);

            booklet.genBooklet();
        }
    }

    /*
     * PDFBook attribute setters.
     */
    public void setPageSize(PDRectangle size) {
        PS = size;
    }

    public void setSheetCount(int count) {
        sheetCount = count;
    }

    public void setFirstPage(int page) {
        if (page < 0) {
            firstPage = 0;

            return;
        }

        if (page > MAX)
            page = MAX;

        if (page > lastPage)
            lastPage = page;

        firstPage = page;
    }

    public void setLastPage(int page) {
        if (page > MAX) {
            lastPage = MAX;

            return;
        }

        if (page < 0)
            page = 0;

        if (page < firstPage)
            firstPage = page;

        lastPage = page;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setRotate(boolean flip) {
        rotate = flip;
    }

    /**
     * Based on the SwingWorker example by "MadProgrammer" here:
     * https://stackoverflow.com/questions/18835835/jprogressbar-not-updating
     */
    public class ProgressWorker extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {

            try {
                inputDoc = PDDocument.load(new File(sourcePDF));

                try {
                    outputDoc = new PDDocument();
                    final int MAX = lastPage;
                    for (int first = firstPage; first < MAX; first += 4 * sheetCount) {
                        int last = first + 4 * sheetCount;
                        if (last > MAX) {
                            last = MAX;
                        }

                        addPDPagesToPdf(first, last);
                        setProgress(100 * last / MAX);
                    }
                    outputDoc.save(outputPDF);
                    if (outputDoc != null) {
                        outputDoc.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                if (inputDoc != null) {
                    inputDoc.close();
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            return null;
        }
    }

    /**
     * Generate a booklet style PDF using a crude images of pages technique.
     */
    public void genBooklet() {
        try {
            inputDoc = PDDocument.load(new File(sourcePDF));

            try {
                outputDoc = new PDDocument();
                final int MAX = lastPage;
                for (int first = firstPage; first < MAX; first += 4 * sheetCount) {
                    int last = first + 4 * sheetCount;
                    if (last > MAX) {
                        last = MAX;
                    }

                    addPDPagesToPdf(first, last);

                    System.out.printf("Pages %d to %d\n", first + 1, last);
                }
                outputDoc.save(outputPDF);
                if (outputDoc != null) {
                    outputDoc.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            if (inputDoc != null) {
                inputDoc.close();
            }

            System.out.println("File created in: " + outputPDF);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Add pages to a PDF document.
     *
     * @param fpn first page number to grab from inputDoc (pages start from 0).
     * @param lpn last page number for grabbing pages BEFORE reaching the last page.
     */
    private void addPDPagesToPdf(int fpn, int lpn) {

        // Create an array of page numbers from a PDF document.
        int i = 0;
        int[] pages = new int[lpn-fpn];
        for (int target = fpn; target < lpn; ++target) {
            pages[i++] = target;
        }

        // Add pages in pairs to both side of the sheet.
        final int LAST = 4 * sheetCount;
        int first = 0;
        int last = LAST - 1;
        for (int sheet = 0; sheet < sheetCount; ++sheet) {
            addPDPagesToPage(pages, first++, last--, false);
            addPDPagesToPage(pages, first++, last--, rotate);
        }
    }

    private void addPDPagesToPage(int[] pages, int top, int bottom,
            boolean flip) {

        if (add2PagesToPage(pages, top, bottom, flip)) {
            try {
                PDPage imported = outputDoc.importPage(page);
                addPageToPdf(imported, false, flip);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            outputDoc.addPage(page);
        }

    }

    /**
     * Add two pages to a page of a PDF document.
     *
     * @param pages array to be added to document in booklet arrangement.
     * @param top index for the top image.
     * @param bottom index for the bottom image.
     * @param flip flag to indicate if the images should be flipped clockwise.
     */
    private boolean add2PagesToPage(int[] pages, int top, int bottom,
            boolean flip) {

        final int count = pages.length;
        boolean tpa = false;
        boolean bpa = false;
        int tpn = 0;
        int bpn = 0;
        if (count > top) {
            tpa = true;
            tpn = pages[top];
        }
        if (count > bottom) {
            bpa = true;
            bpn = pages[bottom];
        }
        if ((tpa == false) && (bpa == false))
            return false;

        try {
            // Create output PDF frame.
            PDRectangle pdf1Frame = inputDoc.getPage(tpn).getCropBox();
            PDRectangle pdf2Frame = inputDoc.getPage(bpn).getCropBox();

            PDRectangle outPdfFrame = new PDRectangle(
                    pdf1Frame.getWidth() + pdf2Frame.getWidth(),
                    Math.max(pdf1Frame.getHeight(), pdf2Frame.getHeight()));

            float tx = (outPdfFrame.getWidth()) / 2;
            float ty = (outPdfFrame.getHeight()) / 2;

            final int idx = outputDoc.getNumberOfPages();


            // Create output page with calculated frame and add it to the document.
            COSDictionary dict = new COSDictionary();
            dict.setItem(COSName.TYPE, COSName.PAGE);
            dict.setItem(COSName.MEDIA_BOX, outPdfFrame);
            dict.setItem(COSName.CROP_BOX, outPdfFrame);
            dict.setItem(COSName.ART_BOX, outPdfFrame);
            page = new PDPage(dict);

            // Source PDF pages has to be imported as form XObjects to be able
            // to insert them at a specific point in the output page.
            LayerUtility layerUtility = new LayerUtility(outputDoc);
            PDFormXObject formPdf1 = layerUtility.importPageAsForm(inputDoc, tpn);
            PDFormXObject formPdf2 = layerUtility.importPageAsForm(inputDoc, bpn);

            // Add form objects to output page.
            if (tpa == true) {
                AffineTransform afLeft = new AffineTransform();
                layerUtility.appendFormAsLayer(page, formPdf1, afLeft, "left" + idx);
            }
            if (bpa == true) {
                AffineTransform afRight = AffineTransform.getTranslateInstance(pdf1Frame.getWidth(), 0.0);
                layerUtility.appendFormAsLayer(page, formPdf2, afRight, "right" + idx);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Add a buffered image to the top or bottom of a page in a PDF document.
     * The image is scaled to fit and centered.
     *
     * @param copyPage to add to document.
     * @param top flag to indicate top or bottom of the page
     * @throws IOException
     */
    private void addPageToPdf(PDPage copyPage, boolean top, boolean clockwise) {

        PDPage outputSize = new PDPage(PS);
        PDPageContentStream stream; // Current stream of "outputDoc".

        final double degrees = clockwise ? 270 : 90;
        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(degrees), 0, 0);

        PDRectangle cropBox = copyPage.getCropBox();
        float tx = (cropBox.getWidth()) / 2;
        float ty = (cropBox.getHeight()) / 2;

        Rectangle rectangle = cropBox.transform(matrix).getBounds();
        float scale = Math.min(cropBox.getWidth() / (float)rectangle.getWidth(), cropBox.getHeight() / (float)rectangle.getHeight());

        try {
            stream = new PDPageContentStream(outputDoc, copyPage, PDPageContentStream.AppendMode.PREPEND, false, false);

            stream.transform(Matrix.getTranslateInstance(tx, ty));
            stream.transform(matrix);
            stream.transform(Matrix.getScaleInstance(scale, scale));

            PDRectangle outputPage = outputSize.getCropBox();

            tx = (cropBox.getHeight() - outputPage.getHeight()) / (2 * scale);
            if (!top)
                tx += (outputPage.getHeight()) / (2 * scale);

            stream.transform(Matrix.getTranslateInstance(-tx, -ty));

            copyPage.setMediaBox(outputSize.getMediaBox());
            copyPage.setCropBox(outputSize.getCropBox());

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
