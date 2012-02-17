TActiveRecord = function(fields) {
    this.Id = null;
       
    for(var field in fields) {
        if(fields.hasOwnProperty(field))
            this[field] = fields[field];
    }
}
    
TActiveRecord.prototype.save = function(successCallback) {
    if (this.Id == null) // let's create object
    {
        var url = tundraServerUrl + "objects/" + tundraProjectId + "/" + this.constructor.tableName;
        $.post(url, JSON.stringify(this), successCallback, "json");
    }
    else // let's update object
    {
      var url = tundraServerUrl + "objects/" + tundraProjectId + "/" + this.constructor.tableName + "/" + this.Id;
      $.ajax({
        type: "PUT",
        url: url,
        data: JSON.stringify(this),
        success: function(){
          successCallback.call(document);
        },
        dataType: "json"
      });
    }
}

TActiveRecord.getAll = function(modelClass, successCallback) {
    var url = tundraServerUrl + "objects/" + tundraProjectId + "/" + modelClass.tableName;
    $.get(url, {}, function(data) {
      objects = [];
      for (var i=0; i< data.length; i++)
      {
        obj = new modelClass(data[i]);
        objects.push(obj);
      }
      successCallback.call(document, objects);
    }, "json");
     
}

TActiveRecord.deleteId = function(modelClass, id, successCallback) {
    var url = tundraServerUrl + "objects/" + tundraProjectId + "/" + modelClass.tableName + "/" + id;
    $.ajax({
      type: "DELETE",
      url: url,
      success: function(){
        successCallback.call(document);
      },
      dataType: "json"
    });
}

TActiveRecord.method = function(modelClass, methodName, methodBody) {
    modelClass.prototype[methodName] = methodBody;
}


TActiveRecord.open = function(modelClass, id, successCallback) {
    var url = tundraServerUrl + "objects/" + tundraProjectId + "/" + modelClass.tableName + "/" + id;

    $.get(url,{}, function(data) {
        var inst = new modelClass(data);
	      successCallback.call(document, inst);
	    }, "json");

}

TActiveRecord.where = function(modelClass, field, predicate, value) {
  var queryObject = new TQueryObject(modelClass);
  return queryObject.where(field, predicate, value);
}

TActiveRecord.order = function(modelClass, field, order) {
  var queryObject = new TQueryObject(modelClass);
  return queryObject.order(field, order);
}

TQueryObject = function(modelClass) {
    this.conditions = [];
    
    this.modelClass = modelClass;
    return this;
}

TQueryObject.prototype.where = function(field, predicate, value) {
    this.conditions.push(new TCondition(field, predicate, value));
    return this;
};

TQueryObject.prototype.order = function(field, order) {
    this.sort = new Object();
    this.sort.fieldName = field;
    this.sort.order = order;
    return this;
};

TQueryObject.prototype.getObjects = function(successCallback) {
    var url = tundraServerUrl + "objects/" + tundraProjectId + "/" +this.modelClass.tableName;
    var modelClass = this.modelClass;
    $.get(url, {jsonParam:JSON.stringify(this.toTransport())}, function(data) {
      objects = [];
      for (var i=0; i< data.length; i++)
      {
        obj = new modelClass(data[i]);
        objects.push(obj);
      }
      successCallback.call(document, objects);
    }, "json");
};

TQueryObject.prototype.toTransport = function() {
    var transport = new Object();
    transport.filter = this.conditions;
    transport.sort = this.sort;
    
    return transport;
};

TCondition = function(field, predicate, value) {
  this.fieldName = field;
  this.conditionType = predicate;
  this.filterValue = value;
}


