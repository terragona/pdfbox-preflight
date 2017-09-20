package com.printmagus.preflight.rule;

import com.printmagus.preflight.Violation;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Formally it was not possiblefor PDF/X-3 to prohibit transparency as it had only been
 * introduced in PDF 1.4 and the PDF 1.4 specification had been published by Adobe
 * after final editing of PDF/X-3 had already been completed. Nevertheless, it was
 * already known what the transparency specification would look like, and in its informal
 * annex to the PDF/X-3 standard itself it is recommended not use transparency as
 * hardly any tool or system that was implemented for PDF 1.3 would be able to successfully
 * handle transparency.
 *
 * Callas technote reference:
 * - Transparency not allowed [PDF/X-1a] [PDF/X-3]
 */
public class NoTransparency extends AbstractRule
{
    @Override
    protected void doValidate(PDDocument document, List<Violation> violations)
    {
        for (COSObject cos: document.getDocument().getObjects()) {
            if (cos.getObject() instanceof COSDictionary) {
                COSDictionary dictionary = (COSDictionary) cos.getObject();

                if (dictionary.containsKey(COSName.S)
                    && dictionary.getDictionaryObject(COSName.S) == COSName.TRANSPARENCY
                ) {
                    System.out.println(cos.getObjectNumber());

                    HashMap<String, Object> context = new HashMap<String, Object>();

                    context.put("transparency", dictionary);

                    String message = null;

                    if (dictionary.containsKey(COSName.TYPE)
                        && dictionary.getDictionaryObject(COSName.TYPE) == COSName.GROUP
                    ) {
                        message = "Transparency not allowed (transparency group)";
                    } else if (dictionary.containsKey(COSName.CS)) {
                        message = "Transparency not allowed (color space)";
                    } else {
                        message = "Transparency not allowed (unknown)";
                    }

                    Violation violation = new Violation(
                        this.getClass().getSimpleName(),
                        message,
                        null,
                        context
                    );

                    violations.add(violation);
                }
            }
        }
    }
}
