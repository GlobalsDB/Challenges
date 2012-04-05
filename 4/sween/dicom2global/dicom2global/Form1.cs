using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Dicom;
using Dicom.Data;
using Dicom.IO;
using InterSystems.Globals;


namespace dicom2global
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            /// Browse for a DICOM File
            openFileDialog1.ShowDialog();
            textBox1.Text = openFileDialog1.FileName.ToString();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            /// Globals Connectivity Here
            /// 
            Connection connection = null;
         try {
            
            connection = ConnectionContext.GetConnection();

           
           if (!connection.IsConnected()) {
               connection.Connect();
            }

           // Open the DICOM File

            DcmDictionary.ImportDictionary("dicom.dic");
            DcmDictionary.ImportDictionary("private.dic");

             var dcmFile = new DicomFileFormat();
             var dcmFileName = textBox1.Text.ToString();
             if (dcmFile.Load(dcmFileName, DicomReadOptions.Default) == DicomReadStatus.Success)
             {

                 var metainfos = dcmFile.FileMetaInfo.Recurse();

                 MessageBox.Show(metainfos.Count().ToString());

             }
            

           // Need to grab the study instance UID and that will be our Root Node

           // NodeReference studyinstanceuid = connection.CreateNodeReference("dicom");
            
           // studyinstanceuid.Set(".1.4.7.2.45.6.9");

           // MetaData Information to meta collection


           // DataSet Information to dataset collection
           
              
             }
             
             catch(Exception ee) {

                 MessageBox.Show(ee.ToString());
             }
             
        }
    }
}
