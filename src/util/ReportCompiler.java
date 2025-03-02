package util;

import net.sf.jasperreports.engine.JasperCompileManager;
import java.io.InputStream;
import java.io.FileOutputStream;

public class ReportCompiler {
    public static void compileReport(String templatePath, String outputPath) throws Exception {
        JasperCompileManager.compileReportToFile(templatePath, outputPath);
        System.out.println("Report compiled successfully: " + outputPath);
    }

    public static void main(String[] args) {
        try {
            // Use ClassLoader to load the template from resources
            ClassLoader classLoader = ReportCompiler.class.getClassLoader();
            InputStream templateStream = classLoader.getResourceAsStream("Wine_report.jrxml");

            if (templateStream == null) {
                throw new IllegalArgumentException("Template not found in resources!");
            }

            // Save the template to a temporary file to compile
            String outputPath = ReportCompiler.class.getProtectionDomain().getCodeSource().getLocation().getPath() 
                                + "report_template.jasper";
            String tempTemplatePath = System.getProperty("java.io.tmpdir") + "Wine_report.jrxml";

            try (FileOutputStream fos = new FileOutputStream(tempTemplatePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = templateStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            // Compile the report
            compileReport(tempTemplatePath, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}