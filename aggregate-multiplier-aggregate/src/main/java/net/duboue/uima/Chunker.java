package net.duboue.uima;

import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.UimaContext;

public class Chunker extends CasAnnotator_ImplBase {

    protected Type type;

    protected String typeName;

    protected String delim;

  public void initialize(UimaContext ctx) throws ResourceInitializationException {
    super.initialize(ctx);

    typeName = (String) getContext().getConfigParameterValue("Type");
    delim = (String) getContext().getConfigParameterValue("Delimiter");
    if(delim.equals("\\n"))
        delim = "\n";
    if(delim.equals(""))
        delim = " ";
  }

    public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException  {
        type = ts.getType(typeName);
        if (type == null) {
            throw new AnalysisEngineProcessException(AnnotatorInitializationException.TYPE_NOT_FOUND,
                                                     new Object[] { getClass().getName(), typeName });
        }
    }

    public void process(CAS cas)  throws AnalysisEngineProcessException{
        String text = cas.getDocumentText();
        //System.out.println("Got " + text.length() + " '" + text + "'");
        int prev = 0;
        int pos = text.indexOf(delim);
        while (pos >= 0) {
            //System.out.println("\t" + prev + " " + pos);
            if (prev == pos) {
                // skip empty chunks
            } else {
                AnnotationFS ann = cas.createAnnotation(type, prev, pos);
                //System.out.println("\t\t\'" + text.substring(prev, pos) + "\'");
                cas.getIndexRepository().addFS(ann);
            }
            prev = pos + delim.length();
            pos = text.indexOf(delim, prev);
        }
        if(prev < text.length() - 1) {
            AnnotationFS ann = cas.createAnnotation(type, prev, text.length());
            //System.out.println("\t\t\'" + text + "\'");
            cas.getIndexRepository().addFS(ann);
        }
    }
}
