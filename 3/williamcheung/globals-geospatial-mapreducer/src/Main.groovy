import groovy.swing.SwingBuilder

import javax.swing.JFrame

// constants used as both labels and field ids
DATASETFILE_ID = 'geospatial dataset file'
FIELDDELIMITER_ID = 'field delimiter'
HASHEADERLINE_ID = 'has header line'
LATITUDECOLUMN_ID = 'latitude column'
LONGITUDECOLUMN_ID = 'longitude column'
DATECOLUMN_ID = 'date column'
MEASUREMENTCOLUMN_ID = 'measurement column'
DATEFORMAT_ID = 'date format'
COMPUTECOUNT_ID = 'count'
COMPUTEMIN_ID = 'min'
COMPUTEMAX_ID = 'max'
COMPUTEAVERAGE_ID = 'average'
GROUPBYMONTH_ID = 'month'
GROUPBYYEAR_ID = 'year'
DEGREESLATITUDE_ID = 'degrees latitude'
DEGREESLONGITUDE_ID = 'degrees longitude'
MAPPERGLOBAL_ID = 'map into Global named'
REDUCERGLOBAL_ID = 'reduce into Global named'
RESULTS_ID = 'results'

builder = new SwingBuilder()
frame = builder.frame(
    title: 'GGSMR - The Globals Big GeoSpatial Data MapReducer',
    show: true,
    size: [800, 600],
    resizable: false,
    locationRelativeTo: null,
    defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
    menuBar {
        menu('Export') {
            menuItem(text: 'export Mapper Output as CSV', actionPerformed: {
                fileChooser = fileChooser(dialogTitle: 'Enter a file to save the Mapper Output as CSV')
                fileChooser.showSaveDialog(frame)
                if (fileChooser.selectedFile) {
                    doOutside {
                        def exporter = new Exporter(builder[MAPPERGLOBAL_ID].text, fileChooser.selectedFile, this)
                        exporter.ignoreLeafNode = true
                        exporter.exportGlobalToCSV()
                    }
                }
            })
            menuItem(text: 'export Reducer Output as CSV', actionPerformed: {
                fileChooser = fileChooser(dialogTitle: 'Enter a file to save the Reducer Output as CSV')
                fileChooser.showSaveDialog(frame)
                if (fileChooser.selectedFile) {
                    doOutside {
                        def exporter = new Exporter(builder[REDUCERGLOBAL_ID].text, fileChooser.selectedFile, this)
                        exporter.ignoreLeafNode = false
                        exporter.exportGlobalToCSV()
                    }
                }
            })
        }
    }
    panel {
        vbox {
            vbox(border: lineBorder(color: java.awt.Color.gray)) {
                panel {
                    label "${DATASETFILE_ID}: "
                    textField(id: DATASETFILE_ID, columns: 30)
                    button(text: 'select', actionPerformed: {
                        fileChooser = fileChooser(dialogTitle: 'Select a geospatial dataset file')
                        fileChooser.showDialog(frame, 'Select')
                        if (fileChooser.selectedFile)
                            builder[DATASETFILE_ID].text = fileChooser.selectedFile.absolutePath
                    })
                }
                panel {
                    hbox {
                        label "${HASHEADERLINE_ID}: "
                        checkBox(id: HASHEADERLINE_ID, selected: true)
                    }
                    hglue()
                    hglue()
                    hbox {
                        label "${FIELDDELIMITER_ID}: "
                        textField(id: FIELDDELIMITER_ID, columns: 2, text: ' ')
                        label ' (space, comma, etc.)'
                    }
                }
                panel {
                    label "${LATITUDECOLUMN_ID}: "
                    textField(id: LATITUDECOLUMN_ID, columns: 2, text: '3')
                    label '(latitude column number, starting from 1)'
                }
                panel {
                    hbox {
                        label "${LONGITUDECOLUMN_ID}: "
                        textField(id: LONGITUDECOLUMN_ID, columns: 2, text: '4')
                    }
                    hglue()
                    hglue()
                    hbox {
                        label "${DATECOLUMN_ID}: "
                        textField(id: DATECOLUMN_ID, columns: 2, text: '5')
                    }
                    hglue()
                    hglue()
                    hbox {
                        label "${MEASUREMENTCOLUMN_ID}: "
                        textField(id: MEASUREMENTCOLUMN_ID, columns: 2, text: '13')
                        label ' (the measurement of interest)'
                    }
                }
                panel {
                    label "${DATEFORMAT_ID}: "
                    textField(id: DATEFORMAT_ID, columns: 6, text: 'M/dd/yyyy')
                    label '(java.text.SimpleDateFormat convention)'
                }
            }
            panel()
            vbox(border: lineBorder(color: java.awt.Color.gray)) {
                panel {
                    label 'compute: '
                    label "${COMPUTECOUNT_ID}"
                    checkBox(id: COMPUTECOUNT_ID, selected: true, enabled: false)
                    label "${COMPUTEMIN_ID}"
                    checkBox(id: COMPUTEMIN_ID, selected: true)
                    label "${COMPUTEMAX_ID}"
                    checkBox(id: COMPUTEMAX_ID, selected: true)
                    label "${COMPUTEAVERAGE_ID}"
                    checkBox(id: COMPUTEAVERAGE_ID, selected: true)
                }
                panel {
                    label 'group by:'
                    buttonGroup().with { group ->
                        radioButton(id: GROUPBYMONTH_ID, text: 'month', buttonGroup: group)
                        radioButton(id: GROUPBYYEAR_ID, text: 'year', buttonGroup: group, selected: true)
                    }
                }
                panel {
                    label 'group into regions of: '
                    textField(id: DEGREESLATITUDE_ID, columns: 2, text: '30')
                    label "${DEGREESLATITUDE_ID} x"
                    textField(id: DEGREESLONGITUDE_ID, columns: 2, text: '30')
                    label "${DEGREESLONGITUDE_ID}"
                }
                panel {
                    label "${MAPPERGLOBAL_ID}: "
                    textField(id: MAPPERGLOBAL_ID, columns: 20, text: 'mapperOutput.yearly.30x30')
                    label "${REDUCERGLOBAL_ID}: "
                    textField(id: REDUCERGLOBAL_ID, columns: 20, text: 'reducerOutput.yearly.30x30')
                }
            }
            panel {
                button(text: 'map reduce', actionPerformed: {
                    def job = new MapReduceJob()
                    job.params = buildJobParameters()
                    job.listener = this
                    doOutside {
                        job.run()
                    }
                })
                button(text: 'delete globals', actionPerformed: {
                    def connection = new ConnectionFactory().createConnection(this)
                    [builder[MAPPERGLOBAL_ID].text, builder[REDUCERGLOBAL_ID].text].each { globalName ->
                        connection.createNodeReference(globalName).kill()
                        notifyStatus("deleted ${globalName}")
                    }
                })
            }
            scrollPane {
                textArea(id: 'results', rows: 12, columns: 60, editable: false)
            }
        }
    }
}

def buildJobParameters() {
    clearResults()
    try {
        def params = new JobParameters(
             dataSetFile: builder[DATASETFILE_ID].text,
             fieldDelimiter: builder[FIELDDELIMITER_ID].text,
             hasHeaderLine: builder[HASHEADERLINE_ID].selected,
             latitudeColumn: builder[LATITUDECOLUMN_ID].text as int,
             longitudeColumn: builder[LONGITUDECOLUMN_ID].text as int,
             dateColumn: builder[DATECOLUMN_ID].text as int,
             measurementColumn: builder[MEASUREMENTCOLUMN_ID].text as int,
             dateFormat: builder[DATEFORMAT_ID].text,
             computeAverage: builder[COMPUTEAVERAGE_ID].selected,
             computeMax: builder[COMPUTEMAX_ID].selected,
             computeMin: builder[COMPUTEMIN_ID].selected,
             groupByMonth: builder[GROUPBYMONTH_ID].selected,
             groupByYear: builder[GROUPBYYEAR_ID].selected,
             degreesLatitude: builder[DEGREESLATITUDE_ID].text as int,
             degreesLongitude: builder[DEGREESLONGITUDE_ID].text as int,
             mapperGlobal: builder[MAPPERGLOBAL_ID].text,
             reducerGlobal: builder[REDUCERGLOBAL_ID].text,
        )
        params
    } catch (e) {
        logError(e)
        throw e
    }
}

def clearResults() {
    builder[RESULTS_ID].text = ''
}

def logError(e) {
    builder[RESULTS_ID].text += "***ERROR*** ${e.class.simpleName}: ${e.localizedMessage}\r\n"
}

// Listener methods

def notifyStatus(message) {
    builder.edt {
        builder[RESULTS_ID].text += "${new Date()} ${message}\r\n"
    }
}

def notifyException(e) {
    builder.edt {
        logError(e)
        throw e
    }
}
