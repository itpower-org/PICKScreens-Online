var PICKScreensJQGrid = function(colNames, colModel, options) {
  options = options || {};
  options.colNames = colNames;
  options.colModel = colModel;
  options.datatype = options.datatype || 'jsonstring';
  options.height = options.height || 'auto';
  options.recordtext = options.recordtext || 'View {0} - {1} of {2}';
  options.emptyrecords = options.emptyrecords || 'No records to view';
  options.loadtext = options.loadtext || 'Loading...';
  options.pgtext = options.pgtext || 'Page {0} of {1}';
  options.multiselect = typeof(options.multiselect) == 'undefined' ? false : options.multiselect;
  options.viewrecords = typeof(options.viewrecords) == 'undefined' ? true : options.viewrecords;
  options.rowNum = options.rowNum || 10;
  options.rowList = options.rowList || [ 10, 20, 30 ];
  options.pager = typeof(options.pager) == 'undefined' ? '#jq-pager' : options.pager;
  options.sortname = options.sortname || colModel[0].name;
  options.sortorder = options.sortorder || 'asc';
  options.caption = options.caption || 'Search results';
  options.shrinkToFit = typeof(options.shrinkToFit) == 'undefined' ? true : options.shrinkToFit;
  options.width = options.width ? options.width : $('#intro').width();
  
  var homepage = new PICKScreensHome();

  /**
   * show the given <code>rows</code> as tables with configured options.
   * if <code>tableId</code> is set, only use table with id for the view.
   * if <code>tableId</code> is not given, show it as a result table.
   * @see PICKScreensHome#showResult
   */
  this.showJSON = function(rows, tableId) {
    if(rows.length > 0) {
      if(!tableId) {
        tableId = 'jq-result';
        if (!$('#page_result .content').hasClass('ui-jqgrid')) {
          $('#page_result .content').addClass('ui-jqgrid');
        }
        homepage.showResult('<table id="jq-result"></table><div id="jq-pager"></div>');
      }
      options.datatype = 'jsonstring';
      options.jsonReader = {
        repeatitems: false
      };
      options.datastr = JSON.stringify(rows);
      var tmpopts = options;
      if(rows.length > 30) tmpopts.rowList.push(rows.length);
      $('#' + tableId).jqGrid(tmpopts);
    } else {
      $('<div>Search result is empty!</div>').dialog({
        title : "Nothing found!",
        modal : true,
        buttons : [ {
          text : "OK",
          click : function() {
            $(this).dialog("close");
          }
        } ]
      });
    }
  }
}