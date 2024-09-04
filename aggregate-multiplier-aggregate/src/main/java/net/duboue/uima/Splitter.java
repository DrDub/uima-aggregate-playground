package net.duboue.uima;

import org.apache.uima.analysis_component.CasMultiplier_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.cas.AbstractCas;
import org.apache.uima.UimaContext;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

public class Splitter extends CasMultiplier_ImplBase {

    protected Type type;

    protected Type srcDocType;

    protected Feature uriFeat;

    protected String typeName;

    protected FSIterator it;

    protected Logger logger;

    protected String docURI;

    protected int counter;

    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);

        logger = getContext().getLogger();
        counter = 0;

        typeName = (String) getContext().getConfigParameterValue("Type");
        it = null;
    }

    public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException  {
        type = ts.getType(typeName);
        srcDocType = ts.getType("org.apache.uima.examples.SourceDocumentInformation");
        uriFeat = ts.getFeatureByFullName("org.apache.uima.examples.SourceDocumentInformation:uri");
        if (type == null) {
            throw new AnalysisEngineProcessException(AnnotatorInitializationException.TYPE_NOT_FOUND,
                                                     new Object[] { getClass().getName(), typeName });
        }
    }

    public void process(CAS cas)  throws AnalysisEngineProcessException{
        counter = 0;
        it = cas.getAnnotationIndex(type).iterator();
        FSIterator itSrc = cas.getAnnotationIndex(srcDocType).iterator();
        if (itSrc.hasNext()) {
            AnnotationFS srcDoc = (AnnotationFS) itSrc.next();
            docURI = srcDoc.getStringValue(uriFeat);
        } else {
            docURI = null;
        }
    }

    public boolean hasNext() throws AnalysisEngineProcessException {
        return it != null && it.hasNext();
    }
    
    public AbstractCas next() throws AnalysisEngineProcessException {
        CAS cas = getEmptyCAS();
        try {
            AnnotationFS annot = (AnnotationFS) it.next();
            String text = annot.getCoveredText();
            cas.setDocumentText(text);
            if (docURI != null) {
                AnnotationFS sdi = cas.createAnnotation(srcDocType, 0, text.length());
                sdi.setStringValue(uriFeat, docURI + "_" + String.valueOf(counter));
                cas.getIndexRepository().addFS(sdi);
            }
            counter++;
            return cas;
        } catch (Exception e) {
            cas.release();
            throw new AnalysisEngineProcessException(e);
        }
    } 
}
