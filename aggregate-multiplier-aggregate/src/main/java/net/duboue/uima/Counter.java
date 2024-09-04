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

import java.io.FileWriter;
import java.io.IOException;

public class Counter extends CasAnnotator_ImplBase {

    protected String filename;
    
    protected int counter;

    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        
        filename = (String) getContext().getConfigParameterValue("Filename");
        counter = 0;
    
        try {
            write();
        }catch(IOException exc) {
            throw new ResourceInitializationException(exc);
        }
    }

    private void write() throws IOException {
        FileWriter fw = new FileWriter(filename);
        fw.write(String.valueOf(counter)+"\n");
        fw.close();
    }

    public void process(CAS cas)  throws AnalysisEngineProcessException{
        counter++;
        try {
            write();
        }catch(IOException exc) {
            throw new AnalysisEngineProcessException(exc);
        }
    }
}
