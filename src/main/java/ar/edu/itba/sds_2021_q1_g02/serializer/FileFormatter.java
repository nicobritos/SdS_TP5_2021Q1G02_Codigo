package ar.edu.itba.sds_2021_q1_g02.serializer;

@FunctionalInterface
public interface FileFormatter {
    String formatFilename(int step);
}
