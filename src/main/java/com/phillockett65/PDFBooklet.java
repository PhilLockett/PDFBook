/*  PDFBooklet - a simple, crude program to generate a booklet from of a PDF.
 *
 *  Copyright 2020 Philip Lockett.
 *
 *  This file is part of PDFBooklet.
 *
 *  PDFBooklet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PDFBooklet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CardGen.  If not, see <https://www.gnu.org/licenses/>.
 */

 /*
 * As a standalone file, PDFBooklet is a simple, crude program to generate a
 * booklet from of a source PDF document. It requires 2 parameters, the source
 * PDF and the name of the new PDF. However, it can be used as a java class, in
 * which case PDFBooklet.main() should be superseded.
 *
 * Example usage:
 *  java -jar path-to-PDFBooklet.jar path-to-source.pdf path-to-new.pdf
 *
 * Dependencies:
 *  PDFbox (pdfbox-app-2.0.19.jar)
 *  https://pdfbox.apache.org/download.cgi
 *
 * Currently this code only supports a single sheet bifolium. In other words, a
 * single sheet containing 4 pages, 2 on each side. In this way, when the sheet
 * is folded in half a booklet is formed. For more information, see:
 *  https://en.wikipedia.org/wiki/Bookbinding#Terms_and_techniques
 *  https://www.formaxprinting.com/blog/2016/11/
 *      booklet-layout-how-to-arrange-the-pages-of-a-saddle-stitched-booklet/
 *  https://www.studentbookbinding.co.uk/blog/
 *      how-to-set-up-pagination-section-sewn-bindings
 *
 * The implementation is crude in that the source pages are captured as images
 * which are then rotated, scaled and arranged on the pages. As a result, the
 * generated document is significantly larger and grainier.
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
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.util.Matrix;

/**
 *
 * @author Phil
 */
public class PDFBooklet {

	private final static String FILE1_PATH = "C:\\Users\\User\\Work\\RedDwarf\\Book2\\Season1.pdf";
	private final static String OUTFILE_PATH = "page.pdf";

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
    private PDPageContentStream stream; // Current stream of "outputDoc".
    private float width;                // "page" width in Points Per Inch.
    private float height;               // "page" height in Points Per Inch.
    private float hHeight;              // Half height.

    // Calculate the Aspect Ratio of half the page (view port).
    private float VPAR;                 // View Port Aspect Ratio.


    /**
     * Constructor.
     *
     * @param inPDF file path for source PDF.
     * @param outPDF file path for generated PDF.
     */
    public PDFBooklet(String inPDF, String outPDF) {
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
//        if (args.length > 1) {
//            PDFBooklet booklet = new PDFBooklet(args[0], args[1]);
//            booklet.setDotsPerInch(300);
//            booklet.setPageSize(PDRectangle.LETTER);
//            booklet.setImageType(ImageType.GRAY);
//
//            booklet.genBooklet();
//        }
            PDFBooklet booklet = new PDFBooklet(FILE1_PATH, OUTFILE_PATH);
            booklet.genBooklet();
//            booklet.generateSideBySidePDF();
    }

    /*
     * PDFBooklet attribute setters.
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

                        int[] pageArray = pdfToPDPageArray(first, last);
                        addPDPagesToPdf(pageArray);
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

                    int[] pageArray = pdfToPDPageArray(first, last);
                    addPDPagesToPdf(pageArray);

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
     * Create an array of images of pages from a PDF document.
     *
     * @param first page to grab from inputDoc (pages start from 0).
     * @param last stop grabbing pages BEFORE reaching the last page.
     * @return a BufferedImage array containing the page images.
     */
    private int[] pdfToPDPageArray(int first, int last) {

        int i = 0;
        int[] pageArray = new int[last-first];
        for (int target = first; target < last; ++target) {
        	pageArray[i++] = target;
        }

        return pageArray;
    }

    /**
     * Add images to a PDF document.
     *
     * @param images array to be added to document in booklet arrangement.
     */
    private void addPDPagesToPdf(int[] pages) {

        final int LAST = 4 * sheetCount;
        int first = 0;
        int last = LAST - 1;
        for (int sheet = 0; sheet < sheetCount; ++sheet) {
            addPDPagesToPage(pages, first++, last--, false);
            addPDPagesToPage(pages, first++, last--, rotate);
        }
    }

    /**
     * Add two images to a page of a PDF document.
     *
     * @param images array to be added to document in booklet arrangement.
     * @param top index for the top image.
     * @param bottom index for the bottom image.
     * @param flip flag to indicate if the images should be flipped clockwise.
     */
    private void addPDPagesToPage(int[] pages, int top, int bottom,
            boolean flip) {

        final int count = pages.length;
        int tpn = 0;
        int bpn = 0;
        if (count > top) {
            tpn = pages[top];
        }
        if (count > bottom) {
            bpn = pages[bottom];
        }

        try {
            // Create output PDF frame
            PDRectangle pdf1Frame = inputDoc.getPage(tpn).getCropBox();
            PDRectangle pdf2Frame = inputDoc.getPage(bpn).getCropBox();

            PDRectangle outPdfFrame = new PDRectangle(pdf1Frame.getWidth()+pdf2Frame.getWidth(), Math.max(pdf1Frame.getHeight(), pdf2Frame.getHeight()));

            // Create output page with calculated frame and add it to the document
            COSDictionary dict = new COSDictionary();
            dict.setItem(COSName.TYPE, COSName.PAGE);
            dict.setItem(COSName.MEDIA_BOX, outPdfFrame);
            dict.setItem(COSName.CROP_BOX, outPdfFrame);
            dict.setItem(COSName.ART_BOX, outPdfFrame);
            PDPage outPdfPage = new PDPage(dict);
            outputDoc.addPage(outPdfPage);
            final int pagesOutput = outputDoc.getNumberOfPages();

            // Source PDF pages has to be imported as form XObjects to be able to insert them at a specific point in the output page
            LayerUtility layerUtility = new LayerUtility(outputDoc);
            PDFormXObject formPdf1 = layerUtility.importPageAsForm(inputDoc, tpn);
            PDFormXObject formPdf2 = layerUtility.importPageAsForm(inputDoc, bpn);

            // Add form objects to output page
            AffineTransform afLeft = new AffineTransform();
            layerUtility.appendFormAsLayer(outPdfPage, formPdf1, afLeft, "left" + pagesOutput);
            AffineTransform afRight = AffineTransform.getTranslateInstance(pdf1Frame.getWidth(), 0.0);
            layerUtility.appendFormAsLayer(outPdfPage, formPdf2, afRight, "right" + pagesOutput);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        final float base = top ? hHeight : 0f;

        final double degrees = clockwise ? 270 : 90;
        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(degrees), 0, 0);

        PDRectangle cropBox = copyPage.getCropBox();
    	float tx = (cropBox.getWidth()) / 2;
    	float ty = (cropBox.getHeight()) / 2;

        Rectangle rectangle = cropBox.transform(matrix).getBounds();
        float scale = Math.min(cropBox.getWidth() / (float)rectangle.getWidth(), cropBox.getHeight() / (float)rectangle.getHeight());

        try {
			stream.transform(Matrix.getTranslateInstance(tx, ty));
			stream.transform(matrix);
			stream.transform(Matrix.getScaleInstance(scale, scale));

	    	PDPage outputSize = new PDPage(PS);
	        PDRectangle outputPage = outputSize.getCropBox();

	        tx = (cropBox.getHeight() - outputPage.getHeight()) / (2 * scale);
	        if (!top)
	        	tx += (outputPage.getHeight()) / (2 * scale);

	    	stream.transform(Matrix.getTranslateInstance(-tx, -ty));

	    	page.setMediaBox(outputSize.getMediaBox());
	        page.setCropBox(outputSize.getCropBox());
        } catch (IOException e) {
			e.printStackTrace();
        }

    }



}
