
def BRANCHES=[
"master",
"feature/development",
...
]

def LIBRARIES=[
"library1" : "pipeline/library/1",
...
]


LIBRARIES.each {
    def name="${it.key}"
    def script="${it.value}"

    pipelineJob("Commerce_pipeline/${name}") {
        description()
        keepDependencies(false)

        parameters {
            labelParam('node') {
                defaultValue('Commerce')
                description('Select nodes')
                allNodes('allCases', 'IgnoreOfflineNodeEligibility')
            }
            choiceParam("BRANCH", BRANCHES, "")
        }
        definition {
            cpsScm {
                lightweight(true)
                scm {
                    git {
                        remote { 
                        url("${GIT_URL}")
                        credentials("${GIT_CREDENTIAL_ID}")
                        }
                        branch("${GIT_BRANCH}")
                    }
                }
                scriptPath("${script}")
            }
        }
        disabled(false)
        configure {
            it / 'properties' / 'com.sonyericsson.rebuild.RebuildSettings' {
                'autoRebuild'('false')
                'rebuildDisabled'('false')
            }
            it / 'properties' / 'jenkins.model.BuildDiscarderProperty' {
                strategy {
                    'daysToKeep'('-1')
                    'numToKeep'('15')
                    'artifactDaysToKeep'('-1')
                    'artifactNumToKeep'('-1')
                }
            }
        }
    }
}

