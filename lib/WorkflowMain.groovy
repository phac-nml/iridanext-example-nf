//
// This file holds several functions specific to the main.nf workflow in the phac-nml/iridanextexample pipeline
//

import nextflow.Nextflow
import java.nio.file.PathMatcher
import java.nio.file.FileSystems
import java.nio.file.Path

class WorkflowMain {

    //
    // Citation string for pipeline
    //
    public static String citation(workflow) {
        return "If you use ${workflow.manifest.name} for your analysis please cite:\n\n" +
            "* The nf-core framework\n" +
            "  https://doi.org/10.1038/s41587-020-0439-x\n\n" +
            "* Software dependencies\n" +
            "  https://github.com/${workflow.manifest.name}/blob/main/CITATIONS.md"
    }


    //
    // Validate parameters and print summary to screen
    //
    public static void initialise(workflow, params, log) {

        // Print workflow version and exit on --version
        if (params.version) {
            String workflow_version = NfcoreTemplate.version(workflow)
            log.info "${workflow.manifest.name} ${workflow_version}"
            System.exit(0)
        }

        // Check that a -profile or Nextflow config has been provided to run the pipeline
        NfcoreTemplate.checkConfigProvided(workflow, log)

        // Check that conda channels are set-up correctly
        if (workflow.profile.tokenize(',').intersect(['conda', 'mamba']).size() >= 1) {
            Utils.checkCondaChannels(log)
        }

        // Check AWS batch settings
        NfcoreTemplate.awsBatch(workflow, params)

        // Check input has been provided
        if (!params.input) {
            Nextflow.error("Please provide an input samplesheet to the pipeline e.g. '--input samplesheet.csv'")
        }
    }

    public static void testMatching(params, log) {
        if (params.outdir != null) {
            Path assembly_file = Nextflow.file(params.outdir) / "assembly" / "SAMPLE1.assembly.fa.gz"
            Path unmatched_file = Nextflow.file(params.outdir) / "assembly" / "unmatched.assemblyN.fa.gz"
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/assembly/*.assembly.fa.gz")
            log.info "testMatching, assembly_file=${assembly_file}, unmatched_file=${unmatched_file}"
            List f = [assembly_file, unmatched_file]
            f.each {
                if(matcher.matches(it)) {
                    log.info "Matched [${it}] using [${matcher}]"
                } else {
                    log.info "Unmatched [${it}] using [${matcher}]"
                }
            }
        }
    }
}
