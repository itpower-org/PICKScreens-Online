var PICKScreensCrossTable = function() {

  if (arguments.callee._singletonInstance)
    return arguments.callee._singletonInstance;
  arguments.callee._singletonInstance = this;

  // construct it
  var homepage = new PICKScreensHome();
  $('button[name=crosstable]').click(getAndShowCrossTable);
  $('#crossTableResultNumber').click(showCrossTableNumber);
  $('#crossTableResultPercentage').click(showCrossTablePercentage);
  var VIEW_FORMAT = {
    NUMBER : 0,
    PERCENTAGE : 1
  };
  var response;

  function getJqGridOptions(crossTable) {
    var result = {};
    // build col names from cross tables horizontal characeristics
    result.colNames = [];
    result.colNames[0] = crossTable.label;
    $(crossTable.horizontalCharacteristics).each(function(i, name) {
      result.colNames[i + 1] = name;
    });

    // build col model from cross tables values
    result.colModel = [];
    result.colModel[0] = {
      name : "id",
      index : "id",
      width : 100
    };
    $(crossTable.values).each(function(i, value) {
      result.colModel[i + 1] = {
        name : '' + i,
        index : '' + i,
        width : 50,
        sortable: true,
        sorttype: 'int'
      };
    });

    // enrich row values
    $(crossTable.values).each(function(i, value) {
      value.id = crossTable.verticalCharacteristics[i];
    });
    return result;
  }
  function showCrossTable(options) {
    options = options || {};
    options.viewFormat = options.viewFormat || VIEW_FORMAT.NUMBER;

    // prepare view
    $('a.crossTableResult').show().attr('disabled', false);
    if (options.viewFormat == VIEW_FORMAT.NUMBER) {
      crossTable = response.rows.num;
      $('#crossTableResultNumber').attr('disabled', true);
    } else { // VIEW == VIEW_FORMAT.PERCENTAGE
      crossTable = response.rows.percentage;
      $('#crossTableResultPercentage').attr('disabled', true);
    }

    // show table
    var jqGridOptions = getJqGridOptions(crossTable);
    var pickscreensJQGrid = new PICKScreensJQGrid(jqGridOptions.colNames, jqGridOptions.colModel);
    pickscreensJQGrid.showJSON(crossTable.values);
    $('a.crossTableResult').show();
  }
  function showCrossTablePercentage() {
    showCrossTable({
      viewFormat : VIEW_FORMAT.PERCENTAGE
    });
  }
  function showCrossTableNumber() {
    showCrossTable({
      viewFormat : VIEW_FORMAT.NUMBER
    });
  }

  function delegateResponse(res) {
    if (res && res.succ) {
      response = res;
      showCrossTable();
    } else {
      homepage.showFail('1304291119');
    }
  }

  function showFail() {
    homepage.showFail('1304231430');
  }

  function getAndShowCrossTable(e) {
    var request = {};
    var ids = homepage.getCheckedScreensIds();
    if (ids.length < PICKScreensConfig.BUTTON_CROSS_TABLE_MIN_SCREENS) {
      $('<div>Need at least ' + PICKScreensConfig.BUTTON_CROSS_TABLE_MIN_SCREENS + ' selected screens!</div>').dialog({
        title : "Hint",
        modal : true,
        buttons : [ {
          text : "OK",
          click : function() {
            $(this).dialog("close");
          }
        } ]
      });
    } else if (ids.length > PICKScreensConfig.BUTTON_CROSS_TABLE_MAX_SCREENS) {
      $('<div>' + PICKScreensConfig.BUTTON_CROSS_TABLE_MAX_SCREENS + ' selected screens at maximum.</div>').dialog({
        title : "Hint",
        modal : true,
        buttons : [ {
          text : "OK",
          click : function() {
            $(this).dialog("close");
          }
        } ]
      });
    } else {
      homepage.showSpinner();
      request.screens = JSON.stringify(ids);
      $.getJSON('crosstable.json', request, delegateResponse).fail(showFail).complete(homepage.hideSpinner);
    }
  }
};