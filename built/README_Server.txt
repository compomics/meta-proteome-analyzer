
   =============================
    MetaProteomeAnalyzer Server
   =============================
	
   In order start the application, please make sure that you have Java 1.7 installed.
   To check your current java version, please type in any console:
   java -version
   
   To start the MetaProteomeAnalyzer Server application please use the following commands:
   java -jar mpa-server-X.Y.Z.jar -Xmx2048m
   (Replace X.Y.Z with the MetaProteomeAnalyzer Server version number.)
    
   In order to keep server running permanently, you could add the "nohup" command in front:
   nohup java -jar mpa-server-X.Y.Z.jar -Xmx2048m
   
   Please make sure to have enough memory on your machine. At least 2 GB should be available for the server version
   The memory can be changed by the -Xmx argument on the command line.
   The default value is set to -Xmx2048m (for 2 GB RAM) in this case.

   ===============================
    MetaProteomeAnalyzer Web Page
   ===============================

   For updated information and documentation (including wiki) about the MetaProteomeAnalyzer project please visit:

   http://meta-proteome-analyzer.googlecode.com


   =========
    License
   =========
   
   Copyright Thilo Muth, Alexander Behne, Robert Heyer, Fabian Kohrs and Lennart Martens.


   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0


   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
   See the License for the specific language governing permissions and
   limitations under the License.


   Please note that some of the JAR files used by the DeNovoGUI may 
   not have the same license as DeNovoGUI itself. If you want to use 
   any of these in a different context, make sure to obtain the original 
   license for the JAR file in question.