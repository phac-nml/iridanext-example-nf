process IRIDA_NEXT_OUTPUT {
    label 'process_single'

    container 'docker.io/python:3.9.17'

    input:
    path(samples_data)

    output:
    path("iridanext.output.json.gz"), emit: output_json
    path("output.csv"), emit: csv
    path("output.json"), emit: json
    path "versions.yml", emit: versions

    when:
    task.ext.when == null || task.ext.when

    script:
    def args = task.ext.args ?: ''
    def samples_data_dir = "samples_data"
    """
    echo "column1,b,c" > output.csv
    echo "SAMPLE1,2,3" >> output.csv
    echo "SAMPLE2,4,5" >> output.csv
    echo "SAMPLE3,6,7" >> output.csv
    echo '{"SAMPLE1": {"json_b": "10", "json_c": "20", "k": {"a": "1", "b": "2"}}, "SAMPLE2": {"colours": ["red", "green"]}}' > output.json
    irida-next-output.py \\
        $args \\
        --summary-file ${task.summary_directory_name}/summary.txt.gz \\
        --json-output iridanext.output.json.gz \\
        ${samples_data}

    cat <<-END_VERSIONS > versions.yml
    "${task.process}":
        iridanextoutput : 0.1.0
    END_VERSIONS
    """
}
