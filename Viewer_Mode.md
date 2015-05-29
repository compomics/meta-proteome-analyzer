<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#General.md'>General</a>
</li><li><a href='#Viewer_Mode.md'>Viewer Mode</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>


---

## General ##


[Go to top of page](#General.md)


---

## Viewer Mode ##

A Java executable jar file called `mpa-viewer-X.Y.Z.jar` can be used to inspect results on work stations that are not connected to the server-client system.
This so called Viewer is not able to perform DB searches, but previously processed and exported experiments can be opened and examined with all functions described in this chapter.
On the server-client system, after processing and fetching results from DB they can be exported using the tab menu (File -> Save Project). This will generate a MGF file and a MPA file. The MPA file alone can be opened in the Viewer, but to get access to any spectrum-based analysis the MGF file has to be placed in the same directory. When starting the Viewer, only the `View Results` panel is available. Using the `Fetch Results from File` button will open a file chooser window that opens previously exported MPA files.


[Go to top of page](#General.md)