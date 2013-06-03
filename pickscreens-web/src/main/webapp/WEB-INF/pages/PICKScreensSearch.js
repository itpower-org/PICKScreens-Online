var PICKScreensSearch = function() {

  if (arguments.callee._singletonInstance)
    return arguments.callee._singletonInstance;
  arguments.callee._singletonInstance = this;

  // construct it
  var defaultTextValue = 'Type in your search here';
  var searchFieldControl = new TextFieldValueControl('input[name=search]', defaultTextValue);
  searchFieldControl.init();
  var homepage = new PICKScreensHome();
  var MIN_QUERY_LENGTH = 3;
  var jqGridOptions = {
    colNames : [ '#', 'Screen', 'Condition Number', 'Salt 1', 'Salt 2', 'Buffer', 'Precipitant 1', 'Precipitant 2' ],
    colModel : [ {
      name : 'id',
      index : 'id',
      width : 30,
      sorttype : 'int'
    }, {
      name : 'screen_name',
      index : 'screen_name',
      width : 150
    }, {
      name : 'condition_number',
      index : 'condition_number',
      width : 20,
      sorttype : 'int'
    }, {
      name : 'salt1',
      index : 'salt1',
      width : 150
    }, {
      name : 'salt2',
      index : 'salt2',
      width : 150
    }, {
      name : 'buffer',
      index : 'buffer',
      width : 150
    }, {
      name : 'precipitant1',
      index : 'precipitant1',
      width : 150
    }, {
      name : 'precipitant2',
      index : 'precipitant2',
      width : 150
    } ]
  };
  var pickscreensJQGrid = new PICKScreensJQGrid(jqGridOptions.colNames, jqGridOptions.colModel);
  $('button[name=search]').click(showSearchResult);
  
  function showJSONAsTable(response) {
    if (response.succ) {
      pickscreensJQGrid.showJSON(response.rows);
    } else {
      homepage.showFail('1304151139');
    }
  }

  function showFail() {
    homepage.showFail('1304151137');
  }

  function showSearchResult(e) {
    var request = {};
    request.query = searchFieldControl.getValue();
    if (request.query.length >= MIN_QUERY_LENGTH) {
      request.screens = JSON.stringify(homepage.getCheckedScreensIds());
      if (request.query !== false) {
        $.getJSON('search.json', request, showJSONAsTable).fail(showFail);
      }
    } else {
      $("<div>Need a query with at least " + MIN_QUERY_LENGTH + " characters!</div>").dialog({
        title : "Hint",
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
};