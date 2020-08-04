# PDFBook

A simple application to generate a booklet from of a PDF. A booklet has two 
pages arranged side-by-side on both side of a sheet of paper such that the 
sheet can be folded to produce a booklet.

## Overview

PDFBook.java can be used as a standalone file or with the GUI front end 
defined in UserGui.java. In both cases it is dependent on PDFbox. This project 
has been set up as a Maven project which includes the GUI and uses Maven to 
resolve the PDFbox dependency. This also means it can be built independent of 
an IDE.

## Command line Usage

PDFBook is a simple application to generate a booklet from of a source 
PDF document. The command line version only uses PDFBook.java and requires 
2 command line parameters, the source PDF and the name of the new PDF. Change 
the "mainClass" in pom.xml from "com.phillockett65.UserGui" to 
"com.phillockett65.PDFBook" then build using maven.

Example usage:

    java -jar ./target/PDFBook-jar-with-dependencies.jar source.pdf new.pdf

## GUI Usage

PDFBook can also be used as an external java class, in which case 
PDFBook.main() should be superseded. UserGui.java is an example that 
instantiates the class, sets the user selected attributes and then executes 
the generator in the background using a SwingWorker.

Maven generates an executable jar file that contains pdfbox-app-2.x.x.jar and 
is named:

    PDFBook-jar-with-dependencies.jar

This can be launched from the command line in the standard way:

    java -jar path-to-jar/PDFBook-jar-with-dependencies.jar

Using the GUI, an Input PDF file can be selected and the booklet version 
generated as a new PDF.

## Cloning and Running the GUI version

The code has been structured as a standard Maven project which means you need 
to have Maven and a JDK installed. A quick web search will help, but if not 
https://maven.apache.org/install.html should guide you through the install.

The following commands clone and generate an executable jar file in the 
"target" directory:

    git clone https://github.com/PhilLockett/PDFBook.git
	cd PDFBook/
    mvn clean install

This jar file can be launched from the command line:

    java -jar ./target/PDFBook-jar-with-dependencies.jar

PDFBook can also be launched using a file explorer.
 
The standard "mvn clean" command will remove all generated files.

## Bookbinding

This code supports multi-sheet sections. For more information on bookbinding 
terms and techniques refer to:
 * [Terms](https://en.wikipedia.org/wiki/Bookbinding#Terms_and_techniques)
 * [Layout](https://www.formaxprinting.com/blog/2016/11/booklet-layout-how-to-arrange-the-pages-of-a-saddle-stitched-booklet/)
 * [Bindings](https://www.studentbookbinding.co.uk/blog/how-to-set-up-pagination-section-sewn-bindings)


## Implementation Summary

For a "Selection Size" of "1 sheet" the document is processed in groups of 4 
pages for each sheet of paper, where each page is captured as a BufferedImage. 
The 4th page is rotated anti-clockwise and scaled to fit on the bottom half of 
one side of the sheet. The 1st page is rotated anti-clockwise and scaled to 
fit on the top half of the same side of the sheet. On the reverse side, the 
2nd page is rotated clockwise and scaled to fit on the top half and the 3rd 
page is rotated clockwise and scaled to fit on the bottom half. This process 
is repeated for all groups of 4 pages in the source document.

For a "Selection Size" of more than 1 sheet, more pages are grouped in 
multiples of 4 and arranged in a similar, but more complex manner.

## Points of interest

This code has the following points of interest:

  * PDFBook is an improved version of PDFBooklet.
  * PDFBook.java was developed as stand-alone code.
  * A user GUI was developed using NetBeans to make using PDFBook easier.
  * The NetBeans UserGui.form file is supplied to ease GUI design changes.
  * The PDF processing can be performed in the background using a SwingWorker.
  * Using a SwingWorker enables a JProgressBar to be supported by the GUI.
