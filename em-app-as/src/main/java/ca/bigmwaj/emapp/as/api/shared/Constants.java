package ca.bigmwaj.emapp.as.api.shared;

public class Constants {
    public final static String FILTER_DOC =
            "The list of operators are: " +
                    "<ul>" +
                    "<li><i><b>like</b></i> stands for greater like. Example: <i>field:like:val</i></li>" +
                    "<li><i><b>gt</b></i> stands for greater than. Example: <i>field:gt:val</i></li>" +
                    "<li><i><b>lt</b></i> stands for lest than. Example: <i>field:eq:lt</i></li>" +
                    "<li><i><b>eq</b></i> stands for equals. Example: <i>field:eq:val</i></li>" +
                    "<li><i><b>ne</b></i> stands for not equals. Example: <i>field:eq:val</i></li>" +
                    "<li><i><b>in</b></i> stands for in. Example: <i>field:eq:val1,val2</i></li>" +
                    "<li><i><b>ni</b></i> stands for not in. Example: <i>field:eq:val1,val2</i></li>" +
                    "<li><i><b>btw</b></i> stands for between. Example: <i>field:btw:val1,val2</i></li>" +
                    "</ul>" +
                    "Notes:" +
                    "<ul>" +
                    "<li>The request value should be in format <i>field1:op:val;field2:op:val</i></li>" +
                    "<li>The date should be in format yyyy-mm-dd</li>" +
                    "</ul>";
}
