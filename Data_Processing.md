<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#Main_View.md'>Main View</a>
</li><li><a href='#Project_Setup.md'>Project Setup</a>
</li><li><a href='#Input_Spectra.md'>Input Spectra</a>
</li><li><a href='#Search_Settings.md'>Search Settings</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>


---

## Main View ##

After starting the MPA client application the main window is displayed.

Navigation through particular steps of the workflow is enabled by a process navigation panel within the main window.

The MPA menu bar holds following functions:
  * **File**
    * _Open Project_ Opens a MPA project file.
    * _Save project_ Saves a MPA project file.
    * _Exit_ Exits the application.
  * **Settings**
    * _Color Settings_ Sets specific color settings (for tables, buttons, panels and progress bars).
    * _Database Connection_ Sets the database connection (SQL server database).
    * _Server Configuration_ Configures the server connection.
  * **Export**
    * _CSV Results_ Exports the results in CSV format (compatible to Excel/Open Office)
    * _GraphML File_ Exports the result set as GraphML (XML-based graph data format)
  * **Help**
    * _Help Contents_ Provides further documentation/help.
    * _About_ Lists the responsible persons (the software developers).

[Go to top of page](#Main_View.md)


---

## Project Setup ##

Main window directly shows the project setup panel. By using the process navigation panel a return to the project setup is possible at any time.

The `New Project` button will open a window in which new projects can be created. Existing projects can be altered via `View/Edit Details` or deleted via `Delete Project`.

Specific properties, e.g. names and values, can be added for further customization of single projects. Since projects might contain several measurements, experiments have to be added which finally will contain the raw data (`Add Experiment`). According to the project creation window, each experiment can be customized with user defined properties or deleted completely (`Delete Experiment`). At any time, properties can be edited via `View/Edit Details`, too.

After finishing project and experiment setup, the file input window can be reached by using the process navigation panel or the `Next` button.

[Go to top of page](#Main_View.md)


---

## Input Spectra ##

There are two ways for loading data files into the MPA within the file input panel. Files of MGF data format can directly be loaded via `Add from File` button. Additionally, data sets that have been processed previously using MASCOT and were exported as DAT files can be put in here as well. A new window will open in which wanted files can be selected, multiple selections are allowed. If local access to a SQL database (DB) exists, it is possible to fetch results from it by using the `Add from DB` button. Again a new window will open that allows fetching data sets by their experiment Identifier (ID). Additionally, it can further be specified, which spectra of an experiment should be fetched (e.g. annotated only).

Chosen data will be shown as tree, adding further files containing data to this window is possible. Table including selected spectra offers several columns that contain specified quantitative information.

After selecting the spectra the search settings panel can be reached using the `Next` button.

[Go to top of page](#Main_View.md)


---

## Search Settings ##

By means of database search experimental MS/MS spectra from sample peptides are compared with theoretical spectra derived by in-silico digestion and fragmentation of proteins from a database in order to identify sample proteins.

First the database for this matching has to be specified and parameters like ion tolerances, number of missed cleavages and the protease itself have to be set.

MPA allows up to four search engines to identify peptides and proteins. All searches can be customized using the `Advanced Settings` button. Those parameters set in the general settings count for each search engine. Finally the search strategy has to be set at `Target Only` or `Target-Decoy`, the latter is implemented as a search in reversed database chosen ahead, which allows to calculate a false discovery rate for the identification.
If MASCOT DAT files have been set up for an experiment, `Mascot` has to be selected as an additional, or exclusive, search engine. Using this option does not repeat the MASCOT search. Actually, DAT files do contain more information than shown to the operator; thereby the `Advanced Settings` options allows specifying certain criteria to filter these information before storing them into the DB.

[Go to top of page](#Main_View.md)