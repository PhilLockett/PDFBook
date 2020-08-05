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
 * This code provides a simple GUI front-end to the PDFBook class and can be
 * used to generate a booklet from of a source PDF document. 
 * 
 * Remember to set the runnable main to UserGui.main().
 * 
 * This code is dependent on PDFBook.java, which itself is dependent on
 * PDFbox (pdfbox-app-2.0.19.jar).
 */
package com.phillockett65;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 *
 * @author Phil
 */
public class UserGui extends javax.swing.JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private PDFBook booklet;
    private int maxPage = 0;
    private String baseDirectory;
    private String sourcePDF;     // The source PDF filepath.
    private String outputPDF;     // The generated PDF filepath.

    /**
     * Creates new form UserGui
     */
    public UserGui() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("acrobat-icon.png")).getImage());
    }

    /**
     * Takes the current selection from the Page Size combo box and converts it
     * to the corresponding PDRectangle value.
     *
     * @return the corresponding PDRectangle value.
     */
    private PDRectangle getPS() {
        switch (pageSizejComboBox.getSelectedItem().toString()) {
            case "A0":      return PDRectangle.A0;
            case "A1":      return PDRectangle.A1;
            case "A2":      return PDRectangle.A2;
            case "A3":      return PDRectangle.A3;
            case "A4":      return PDRectangle.A4;
            case "A5":      return PDRectangle.A5;
            case "A6":      return PDRectangle.A6;
            case "Legal":   return PDRectangle.LEGAL;
            case "Letter":  return PDRectangle.LETTER;
        }

        return PDRectangle.LETTER;
    }

    /**
     * Returns the current selection from flipReverseSidejCheckBox.
     *
     * @return the flipReverseSidejCheckBox state.
     */
    private boolean getFlipReverseSide() {
        return flipReverseSidejCheckBox.isSelected();
    }


    /**
     * Takes the current selection from the Section Size combo box and converts
     * it to the corresponding int value.
     *
     * @return the corresponding PDRectangle value.
     */
    private int getSheetCount() {
        final int sel = sectionSizejComboBox.getSelectedIndex();
        return sel + 1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourcePDFjLabel = new javax.swing.JLabel();
        sourcePDFjTextField = new javax.swing.JTextField();
        browsejButton = new javax.swing.JButton();
        pageSizejLabel = new javax.swing.JLabel();
        pageSizejComboBox = new javax.swing.JComboBox<>();
        flipReverseSidejLabel = new javax.swing.JLabel();
        flipReverseSidejCheckBox = new javax.swing.JCheckBox();
        outputPDFjLabel = new javax.swing.JLabel();
        outputPDFjTextField = new javax.swing.JTextField();
        generatejButton = new javax.swing.JButton();
        sectionSizejLabel = new javax.swing.JLabel();
        sectionSizejComboBox = new javax.swing.JComboBox<>();
        pagesjLabel = new javax.swing.JLabel();
        generatejProgressBar = new javax.swing.JProgressBar();
        firstPagejLabel = new javax.swing.JLabel();
        firstPagejSpinner = new javax.swing.JSpinner();
        lastPagejLabel = new javax.swing.JLabel();
        lastPagejSpinner = new javax.swing.JSpinner();
        pageCountjLabel = new javax.swing.JLabel();
        outputjLabel = new javax.swing.JLabel();
        backgroundjLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PDF Booklet Generator 1.0");
        setPreferredSize(new java.awt.Dimension(670, 280));
        getContentPane().setLayout(null);

        sourcePDFjLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sourcePDFjLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sourcePDFjLabel.setText("Source Document:");
        sourcePDFjLabel.setToolTipText("File path to the Source PDF Document.");
        getContentPane().add(sourcePDFjLabel);
        sourcePDFjLabel.setBounds(20, 10, 130, 17);

        sourcePDFjTextField.setEditable(false);
        sourcePDFjTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sourcePDFjTextField.setText(".");
        getContentPane().add(sourcePDFjTextField);
        sourcePDFjTextField.setBounds(170, 10, 352, 23);

        browsejButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        browsejButton.setText("Browse...");
        browsejButton.setToolTipText("Select the Source PDF Document.");
        browsejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browsejButtonActionPerformed(evt);
            }
        });
        getContentPane().add(browsejButton);
        browsejButton.setBounds(540, 10, 89, 25);

        pageSizejLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pageSizejLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pageSizejLabel.setText("Output Page Size:");
        pageSizejLabel.setToolTipText("Page Size of the generated PDF document.");
        getContentPane().add(pageSizejLabel);
        pageSizejLabel.setBounds(20, 50, 130, 17);

        pageSizejComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pageSizejComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A0", "A1", "A2", "A3", "A4", "A5", "A6", "Legal", "Letter" }));
        pageSizejComboBox.setSelectedIndex(8);
        getContentPane().add(pageSizejComboBox);
        pageSizejComboBox.setBounds(170, 50, 88, 23);

        flipReverseSidejLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        flipReverseSidejLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        flipReverseSidejLabel.setText("Flip Reverse Side:");
        flipReverseSidejLabel.setToolTipText("Rotate reverse side of sheet.");
        getContentPane().add(flipReverseSidejLabel);
        flipReverseSidejLabel.setBounds(40, 90, 110, 17);

        flipReverseSidejCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        flipReverseSidejCheckBox.setSelected(true);
        getContentPane().add(flipReverseSidejCheckBox);
        flipReverseSidejCheckBox.setBounds(170, 90, 21, 21);

        outputPDFjLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        outputPDFjLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        outputPDFjLabel.setText("Output File Name:");
        outputPDFjLabel.setToolTipText("Output file name, pdf extension will be added.");
        getContentPane().add(outputPDFjLabel);
        outputPDFjLabel.setBounds(340, 50, 120, 17);

        outputPDFjTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        outputPDFjTextField.setText("booklet");
        getContentPane().add(outputPDFjTextField);
        outputPDFjTextField.setBounds(470, 50, 156, 23);

        generatejButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        generatejButton.setText("Generate");
        generatejButton.setToolTipText("Select the Source PDF Document first.");
        generatejButton.setEnabled(false);
        generatejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generatejButtonActionPerformed(evt);
            }
        });
        getContentPane().add(generatejButton);
        generatejButton.setBounds(520, 160, 103, 31);

        sectionSizejLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sectionSizejLabel.setText("Section Size:");
        sectionSizejLabel.setToolTipText("Number of sheets of paper per section.");
        getContentPane().add(sectionSizejLabel);
        sectionSizejLabel.setBounds(70, 160, 78, 17);

        sectionSizejComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sectionSizejComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 sheet", "2 sheets", "3 sheets", "4 sheets", "5 sheets", "6 sheets" }));
        sectionSizejComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sectionSizejComboBoxActionPerformed(evt);
            }
        });
        getContentPane().add(sectionSizejComboBox);
        sectionSizejComboBox.setBounds(170, 160, 88, 23);

        pagesjLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pagesjLabel.setText("(4 pages)");
        getContentPane().add(pagesjLabel);
        pagesjLabel.setBounds(280, 160, 110, 17);

        generatejProgressBar.setOpaque(true);
        generatejProgressBar.setStringPainted(true);
        getContentPane().add(generatejProgressBar);
        generatejProgressBar.setBounds(350, 90, 280, 20);

        firstPagejLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        firstPagejLabel.setText("First Page:");
        firstPagejLabel.setToolTipText("First page to add to the booklet.");
        getContentPane().add(firstPagejLabel);
        firstPagejLabel.setBounds(80, 120, 70, 17);

        firstPagejSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 1, 1));
        firstPagejSpinner.setEnabled(false);
        firstPagejSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                firstPagejSpinnerStateChanged(evt);
            }
        });
        getContentPane().add(firstPagejSpinner);
        firstPagejSpinner.setBounds(170, 120, 60, 30);

        lastPagejLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lastPagejLabel.setText("Last Page:");
        lastPagejLabel.setToolTipText("Last page to add to the booklet.");
        getContentPane().add(lastPagejLabel);
        lastPagejLabel.setBounds(270, 120, 70, 17);

        lastPagejSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 1, 1));
        lastPagejSpinner.setEnabled(false);
        lastPagejSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lastPagejSpinnerStateChanged(evt);
            }
        });
        getContentPane().add(lastPagejSpinner);
        lastPagejSpinner.setBounds(360, 120, 60, 30);

        pageCountjLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pageCountjLabel.setToolTipText("");
        getContentPane().add(pageCountjLabel);
        pageCountjLabel.setBounds(460, 130, 170, 17);

        outputjLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        getContentPane().add(outputjLabel);
        outputjLabel.setBounds(70, 220, 550, 20);

        backgroundjLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        backgroundjLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/phillockett65/background.jpg"))); // NOI18N
        backgroundjLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(backgroundjLabel);
        backgroundjLabel.setBounds(-5, -6, 680, 270);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Acts on the "Generate" button click event.
     *
     * @param evt the event that triggered the handler.
     */
    private void generatejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generatejButtonActionPerformed

        outputPDF = baseDirectory + "\\" + outputPDFjTextField.getText()
                + ".pdf";

        booklet = new PDFBook(sourcePDF, outputPDF);

        booklet.setPageSize(getPS());
        booklet.setRotate(getFlipReverseSide());
        booklet.setSheetCount(getSheetCount());

        final int first = (Integer)firstPagejSpinner.getValue();
        final int last = (Integer)lastPagejSpinner.getValue();
        booklet.setFirstPage(first-1);
        booklet.setLastPage(last);

        generatejButton.setEnabled(false);
        outputjLabel.setText("");

        // Use PDFBook.ProgressWorker to generate PDF in the background and
        // update the progress bar as we go.
        PDFBook.ProgressWorker pw = booklet.new ProgressWorker();
        pw.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("progress")) {
                    int progress = (int) evt.getNewValue();
                    generatejProgressBar.setValue(progress);
                    repaint();
                } else if (name.equals("state")) {
                    SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                    switch (state) {
                        case DONE:
                            generatejButton.setEnabled(true);

                            outputjLabel.setText("File created in: " + outputPDF);
                            break;
                        default:
                            break;
                    }
                }
            }

        });
        pw.execute();

    }//GEN-LAST:event_generatejButtonActionPerformed

    private void updatePageCountjLabel() {
        Integer count = (Integer)lastPagejSpinner.getValue();
        count -= (Integer)firstPagejSpinner.getValue();
        count += 1;
        pageCountjLabel.setText("(" + count.toString() + " total pages)");
    }

    /**
     * Find the maximum page number of the selected source PDF document.
     */
    private void setMaxPage() {
        PDFBook temp = new PDFBook(sourcePDF, outputPDF);
        maxPage = temp.getMaxPage();
        if (maxPage > 0) {
            firstPagejSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, maxPage, 1));
            firstPagejSpinner.setEnabled(true);
            lastPagejSpinner.setModel(new javax.swing.SpinnerNumberModel(maxPage, 1, maxPage, 1));
            lastPagejSpinner.setEnabled(true);
            updatePageCountjLabel();
        }
    }

    /**
     * Select a source PDF file using a JFileChooser.
     *
     * @return true if a PDF file is selected, false otherwise.
     */
    private boolean selectSourcePDF() {
        // Set up the file selector.
        JFileChooser choice = new JFileChooser(baseDirectory);
        choice.setDialogTitle("Select source PDF document");
        choice.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter;
        filter = new FileNameExtensionFilter("PDF Files", "pdf");
        choice.setFileFilter(filter);
        choice.setAcceptAllFileFilterUsed(false);

        // Launch the file selector.
        int selected = choice.showOpenDialog(this);
        if (selected == JFileChooser.APPROVE_OPTION) {
            // Verify the file selection.
            File source = choice.getSelectedFile();
            baseDirectory = source.getParent();
            if (source.isFile()) {
                sourcePDF = source.getPath();
                sourcePDFjTextField.setText(sourcePDF);
                setMaxPage();

                return true;
            }
        }

        return false;
    }

    /**
     * Acts on the "Browse..." button click event and enables the "Generate"
     * button if a valid source PDF file is selected.
     *
     * @param evt the event that triggered the handler.
     */
    private void browsejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browsejButtonActionPerformed
        final boolean selected = selectSourcePDF();
        if (selected) {
            generatejButton.setEnabled(true);
            generatejButton.setToolTipText("Generate the PDF in booklet form.");
        } else {
            generatejButton.setEnabled(false);
            generatejButton.setToolTipText("Select the Source Document first.");
        }
    }//GEN-LAST:event_browsejButtonActionPerformed

    private void sectionSizejComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sectionSizejComboBoxActionPerformed

        final int pages = getSheetCount() * 4;
        pagesjLabel.setText("(" + pages + " pages)");
    }//GEN-LAST:event_sectionSizejComboBoxActionPerformed

    private void firstPagejSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_firstPagejSpinnerStateChanged
        final int min = (Integer)firstPagejSpinner.getValue();
        final int current = (Integer)lastPagejSpinner.getValue();
        lastPagejSpinner.setModel(new javax.swing.SpinnerNumberModel(current, min, maxPage, 1));
        updatePageCountjLabel();
    }//GEN-LAST:event_firstPagejSpinnerStateChanged

    private void lastPagejSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lastPagejSpinnerStateChanged
        final int max = (Integer)lastPagejSpinner.getValue();
        final int current = (Integer)firstPagejSpinner.getValue();
        firstPagejSpinner.setModel(new javax.swing.SpinnerNumberModel(current, 1, max, 1));
        updatePageCountjLabel();
    }//GEN-LAST:event_lastPagejSpinnerStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backgroundjLabel;
    private javax.swing.JButton browsejButton;
    private javax.swing.JLabel firstPagejLabel;
    private javax.swing.JSpinner firstPagejSpinner;
    private javax.swing.JCheckBox flipReverseSidejCheckBox;
    private javax.swing.JLabel flipReverseSidejLabel;
    private javax.swing.JButton generatejButton;
    private javax.swing.JProgressBar generatejProgressBar;
    private javax.swing.JLabel lastPagejLabel;
    private javax.swing.JSpinner lastPagejSpinner;
    private javax.swing.JLabel outputPDFjLabel;
    private javax.swing.JTextField outputPDFjTextField;
    private javax.swing.JLabel outputjLabel;
    private javax.swing.JLabel pageCountjLabel;
    private javax.swing.JComboBox<String> pageSizejComboBox;
    private javax.swing.JLabel pageSizejLabel;
    private javax.swing.JLabel pagesjLabel;
    private javax.swing.JComboBox<String> sectionSizejComboBox;
    private javax.swing.JLabel sectionSizejLabel;
    private javax.swing.JLabel sourcePDFjLabel;
    private javax.swing.JTextField sourcePDFjTextField;
    // End of variables declaration//GEN-END:variables
}
