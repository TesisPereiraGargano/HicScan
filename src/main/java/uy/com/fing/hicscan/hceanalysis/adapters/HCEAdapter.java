package uy.com.fing.hicscan.hceanalysis.adapters;

import java.io.File;
import java.io.IOException;

public interface HCEAdapter {
    void parse(File file) throws IOException;
}

