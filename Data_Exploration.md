<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#General.md'>General</a>
</li><li><a href='#Dynamic_Grouping.md'>Dynamic Grouping</a>
</li><li><a href='#Export_Results.md'>Export Results</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>


---

## General ##



[Go to top of page](#General.md)


---

## Dynamic Grouping ##
During manual data evaluation using those tools described previously  it might happen that open questions are left. Furthermore, some very specific questions might not be able to be answered. For these cases the software provides a graph-based DB that allows inspection of derived results in a much more specific manner.

Using the Process Navigation Panel, GraphDB Results panel can be found following View Results. The now blank page titled `GraphDB Content` will show a set of hits resulting from the later query. Pushing the `GraphDB Query` button will open the GraphDB Query Dialog, which is divided into three panels:
  * _Predefined Queries_
  * _Compound Queries_
  * _Cypher Console_

Text shown in the Cypher Console serves as input for the DB query. All queries assembled as Compound Queries or chosen from any Predefined Queries will be translated into Cypher text automatically. Thus, these three panels provide an increasing degree of difficulty for handling, but at the same moment allow more complex query assemblies. It is recommended to start working with Predefined Queries first to thereby learn the syntax of the available Compound Query and the console. For later applications it is possible to save altered or customized queries from the console using the `Save...` button.
Saved Queries will be available on the local MPA client only. When query assembly is finished, using the `Execute` button will close the current window and show the respective results in the GraphDB Content window. If a wrong syntax has been chosen for a query, an alert will appear and no query is sent to the GraphDB. If a query would return no results from the data set but was syntactically right, this will be displayed as message, too.

[Go to top of page](#General.md)


---

## Export Results ##



[Go to top of page](#General.md)