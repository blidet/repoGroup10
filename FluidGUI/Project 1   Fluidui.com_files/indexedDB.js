var idbModules={};(function(b){function e(g,f,i,h){i.target=f;(typeof f[g]==="function")&&f[g].apply(f,[i]);(typeof h==="function")&&h()}function d(g,h,f){var i=new DOMException.constructor(0,h);i.name=g;i.message=h;i.stack=arguments.callee.caller;b.DEBUG&&console.log(g,h,f,i);throw i}var c=function(){this.length=0;this._items=[];if(Object.defineProperty){Object.defineProperty(this,"_items",{enumerable:false})}};c.prototype={contains:function(f){return -1!==this._items.indexOf(f)},item:function(f){return this._items[f]},indexOf:function(f){return this._items.indexOf(f)},push:function(g){this._items.push(g);this.length+=1;for(var f=0;f<this._items.length;f++){this[f]=this._items[f]}},splice:function(){this._items.splice.apply(this._items,arguments);this.length=this._items.length;for(var f in this){if(f===String(parseInt(f,10))){delete this[f]}}for(f=0;f<this._items.length;f++){this[f]=this._items[f]}}};if(Object.defineProperty){for(var a in {"indexOf":false,"push":false,"splice":false}){Object.defineProperty(c.prototype,a,{enumerable:false})}}b.util={"throwDOMException":d,"callback":e,"quote":function(f){return"'"+f+"'"},"StringList":c}}(idbModules));(function(a){var b=(function(){return{"encode":function(c){return JSON.stringify(c)},"decode":function(c){return JSON.parse(c)}}}());a.Sca=b}(idbModules));(function(c){var e=["","number","string","boolean","object","undefined"];var d=function(){return{"encode":function(f){return e.indexOf(typeof f)+"-"+JSON.stringify(f)},"decode":function(f){if(typeof f==="undefined"){return undefined}else{return JSON.parse(f.substring(2))}}}};var b={"number":d("number"),"boolean":d(),"object":d(),"string":{"encode":function(f){return e.indexOf("string")+"-"+f},"decode":function(f){return""+f.substring(2)}},"undefined":{"encode":function(f){return e.indexOf("undefined")+"-undefined"},"decode":function(f){return undefined}}};var a=(function(){return{encode:function(f){return b[typeof f].encode(f)},decode:function(f){return b[e[f.substring(0,1)]].decode(f)}}}());c.Key=a}(idbModules));(function(b,c){var a=function(e,d){return{"type":e,debug:d,bubbles:false,cancelable:false,eventPhase:0,timeStamp:new Date()}};b.Event=a}(idbModules));(function(b){var a=function(){this.onsuccess=this.onerror=this.result=this.error=this.source=this.transaction=null;this.readyState="pending"};var c=function(){this.onblocked=this.onupgradeneeded=null};c.prototype=a;b.IDBRequest=a;b.IDBOpenRequest=c}(idbModules));(function(b,c){var a=function(e,f,d,g){this.lower=e;this.upper=f;this.lowerOpen=d;this.upperOpen=g};a.only=function(d){return new a(d,d,true,true)};a.lowerBound=function(e,d){return new a(e,c,d,c)};a.upperBound=function(d){return new a(c,d,c,open)};a.bound=function(e,f,d,g){return new a(e,f,d,g)};b.IDBKeyRange=a}(idbModules));(function(a,c){function b(d,h,e,i,g,f){this.__range=d;this.source=this.__idbObjectStore=e;this.__req=i;this.key=c;this.direction=h;this.__keyColumnName=g;this.__valueColumnName=f;if(!this.source.transaction.__active){a.util.throwDOMException("TransactionInactiveError - The transaction this IDBObjectStore belongs to is not active.")}this.__offset=-1;this.__lastKeyContinued=c;this["continue"]()}b.prototype.__find=function(f,d,j,e){var g=this;var i=["SELECT * FROM ",a.util.quote(g.__idbObjectStore.name)];var h=[];i.push("WHERE ",g.__keyColumnName," NOT NULL");if(g.__range&&(g.__range.lower||g.__range.upper)){i.push("AND");if(g.__range.lower){i.push(g.__keyColumnName+(g.__range.lowerOpen?" >":" >= ")+" ?");h.push(a.Key.encode(g.__range.lower))}(g.__range.lower&&g.__range.upper)&&i.push("AND");if(g.__range.upper){i.push(g.__keyColumnName+(g.__range.upperOpen?" < ":" <= ")+" ?");h.push(a.Key.encode(g.__range.upper))}}if(typeof f!=="undefined"){g.__lastKeyContinued=f;g.__offset=0}if(g.__lastKeyContinued!==c){i.push("AND "+g.__keyColumnName+" >= ?");h.push(a.Key.encode(g.__lastKeyContinued))}i.push("ORDER BY ",g.__keyColumnName);i.push("LIMIT 1 OFFSET "+g.__offset);a.DEBUG&&console.log(i.join(" "),h);d.executeSql(i.join(" "),h,function(k,m){if(m.rows.length===1){var l=a.Key.decode(m.rows.item(0)[g.__keyColumnName]);var n=g.__valueColumnName==="value"?a.Sca.decode(m.rows.item(0)[g.__valueColumnName]):a.Key.decode(m.rows.item(0)[g.__valueColumnName]);j(l,n)}else{a.DEBUG&&console.log("Reached end of cursors");j(c,c)}},function(k,l){a.DEBUG&&console.log("Could not execute Cursor.continue");e(l)})};b.prototype["continue"]=function(d){var e=this;this.__idbObjectStore.transaction.__addToTransactionQueue(function(f,h,i,g){e.__offset++;e.__find(d,f,function(j,k){e.key=j;e.value=k;i(typeof e.key!=="undefined"?e:c,e.__req)},function(j){g(j)})})};b.prototype.advance=function(e){if(e<=0){a.util.throwDOMException("Type Error - Count is invalid - 0 or negative",e)}var d=this;this.__idbObjectStore.transaction.__addToTransactionQueue(function(f,h,i,g){d.__offset+=e;d.__find(c,f,function(j,k){d.key=j;d.value=k;i(typeof d.key!=="undefined"?d:c,d.__req)},function(j){g(j)})})};b.prototype.update=function(e){var d=this;return this.__idbObjectStore.transaction.__addToTransactionQueue(function(f,h,i,g){d.__find(c,f,function(j,k){var l="UPDATE "+a.util.quote(d.__idbObjectStore.name)+" SET value = ? WHERE key = ?";a.DEBUG&&console.log(l,e,j);f.executeSql(l,[a.Sca.encode(e),a.Key.encode(j)],function(m,n){if(n.rowsAffected===1){i(j)}else{g("No rowns with key found"+j)}},function(m,n){g(n)})},function(j){g(j)})})};b.prototype["delete"]=function(){var d=this;return this.__idbObjectStore.transaction.__addToTransactionQueue(function(e,g,h,f){d.__find(c,e,function(i,j){var k="DELETE FROM  "+a.util.quote(d.__idbObjectStore.name)+" WHERE key = ?";a.DEBUG&&console.log(k,i);e.executeSql(k,[a.Key.encode(i)],function(l,m){if(m.rowsAffected===1){h(c)}else{f("No rowns with key found"+i)}},function(l,m){f(m)})},function(i){f(i)})})};a.IDBCursor=b}(idbModules));(function(idbModules,undefined){function IDBIndex(indexName,idbObjectStore){this.indexName=this.name=indexName;this.__idbObjectStore=this.objectStore=this.source=idbObjectStore;var indexList=idbObjectStore.__storeProps&&idbObjectStore.__storeProps.indexList;indexList&&(indexList=JSON.parse(indexList));this.keyPath=((indexList&&indexList[indexName]&&indexList[indexName].keyPath)||indexName);["multiEntry","unique"].forEach(function(prop){this[prop]=!!indexList&&!!indexList[indexName]&&!!indexList[indexName].optionalParams&&!!indexList[indexName].optionalParams[prop]},this)}IDBIndex.prototype.__createIndex=function(indexName,keyPath,optionalParameters){var me=this;var transaction=me.__idbObjectStore.transaction;transaction.__addToTransactionQueue(function(tx,args,success,failure){me.__idbObjectStore.__getStoreProps(tx,function(){function error(){idbModules.util.throwDOMException(0,"Could not create new index",arguments)}if(transaction.mode!==2){idbModules.util.throwDOMException(0,"Invalid State error, not a version transaction",me.transaction)}var idxList=JSON.parse(me.__idbObjectStore.__storeProps.indexList);if(typeof idxList[indexName]!=="undefined"){idbModules.util.throwDOMException(0,"Index already exists on store",idxList)}var columnName=indexName;idxList[indexName]={"columnName":columnName,"keyPath":keyPath,"optionalParams":optionalParameters};me.__idbObjectStore.__storeProps.indexList=JSON.stringify(idxList);var sql=["ALTER TABLE",idbModules.util.quote(me.__idbObjectStore.name),"ADD",columnName,"BLOB"].join(" ");idbModules.DEBUG&&console.log(sql);tx.executeSql(sql,[],function(tx,data){tx.executeSql("SELECT * FROM "+idbModules.util.quote(me.__idbObjectStore.name),[],function(tx,data){(function initIndexForRow(i){if(i<data.rows.length){try{var value=idbModules.Sca.decode(data.rows.item(i).value);var indexKey=eval("value['"+keyPath+"']");tx.executeSql("UPDATE "+idbModules.util.quote(me.__idbObjectStore.name)+" set "+columnName+" = ? where key = ?",[idbModules.Key.encode(indexKey),data.rows.item(i).key],function(tx,data){initIndexForRow(i+1)},error)}catch(e){initIndexForRow(i+1)}}else{idbModules.DEBUG&&console.log("Updating the indexes in table",me.__idbObjectStore.__storeProps);tx.executeSql("UPDATE __sys__ set indexList = ? where name = ?",[me.__idbObjectStore.__storeProps.indexList,me.__idbObjectStore.name],function(){me.__idbObjectStore.__setReadyState("createIndex",true);success(me)},error)}}(0))},error)},error)},"createObjectStore")})};IDBIndex.prototype.openCursor=function(range,direction){var cursorRequest=new idbModules.IDBRequest();var cursor=new idbModules.IDBCursor(range,direction,this.source,cursorRequest,this.indexName,"value");return cursorRequest};IDBIndex.prototype.openKeyCursor=function(range,direction){var cursorRequest=new idbModules.IDBRequest();var cursor=new idbModules.IDBCursor(range,direction,this.source,cursorRequest,this.indexName,"key");return cursorRequest};IDBIndex.prototype.__fetchIndexData=function(key,opType){var me=this;return me.__idbObjectStore.transaction.__addToTransactionQueue(function(tx,args,success,error){var sql=["SELECT * FROM ",idbModules.util.quote(me.__idbObjectStore.name)," WHERE",me.indexName,"NOT NULL"];var sqlValues=[];if(typeof key!=="undefined"){sql.push("AND",me.indexName," = ?");sqlValues.push(idbModules.Key.encode(key))}idbModules.DEBUG&&console.log("Trying to fetch data for Index",sql.join(" "),sqlValues);tx.executeSql(sql.join(" "),sqlValues,function(tx,data){var d;if(typeof opType==="count"){d=data.rows.length}else{if(data.rows.length===0){d=undefined}else{if(opType==="key"){d=idbModules.Key.decode(data.rows.item(0).key)}else{d=idbModules.Sca.decode(data.rows.item(0).value)}}}success(d)},error)})};IDBIndex.prototype.get=function(key){return this.__fetchIndexData(key,"value")};IDBIndex.prototype.getKey=function(key){return this.__fetchIndexData(key,"key")};IDBIndex.prototype.count=function(key){return this.__fetchIndexData(key,"count")};idbModules.IDBIndex=IDBIndex}(idbModules));(function(idbModules){var IDBObjectStore=function(name,idbTransaction,ready){this.name=name;this.transaction=idbTransaction;this.__ready={};this.__setReadyState("createObjectStore",typeof ready==="undefined"?true:ready);this.indexNames=new idbModules.util.StringList()};IDBObjectStore.prototype.__setReadyState=function(key,val){this.__ready[key]=val};IDBObjectStore.prototype.__waitForReady=function(callback,key){var ready=true;if(typeof key!=="undefined"){ready=(typeof this.__ready[key]==="undefined")?true:this.__ready[key]}else{for(var x in this.__ready){if(!this.__ready[x]){ready=false}}}if(ready){callback()}else{idbModules.DEBUG&&console.log("Waiting for to be ready",key);var me=this;window.setTimeout(function(){me.__waitForReady(callback,key)},100)}};IDBObjectStore.prototype.__getStoreProps=function(tx,callback,waitOnProperty){var me=this;this.__waitForReady(function(){if(me.__storeProps){idbModules.DEBUG&&console.log("Store properties - cached",me.__storeProps);callback(me.__storeProps)}else{tx.executeSql("SELECT * FROM __sys__ where name = ?",[me.name],function(tx,data){if(data.rows.length!==1){callback()}else{me.__storeProps={"name":data.rows.item(0).name,"indexList":data.rows.item(0).indexList,"autoInc":data.rows.item(0).autoInc,"keyPath":data.rows.item(0).keyPath};idbModules.DEBUG&&console.log("Store properties",me.__storeProps);callback(me.__storeProps)}},function(){callback()})}},waitOnProperty)};IDBObjectStore.prototype.__deriveKey=function(tx,value,key,callback){function getNextAutoIncKey(){tx.executeSql("SELECT * FROM sqlite_sequence where name like ?",[me.name],function(tx,data){if(data.rows.length!==1){callback(0)}else{callback(data.rows.item(0).seq)}},function(tx,error){idbModules.util.throwDOMException(0,"Data Error - Could not get the auto increment value for key",error)})}var me=this;me.__getStoreProps(tx,function(props){if(!props){idbModules.util.throwDOMException(0,"Data Error - Could not locate defination for this table",props)}if(props.keyPath){if(typeof key!=="undefined"){idbModules.util.throwDOMException(0,"Data Error - The object store uses in-line keys and the key parameter was provided",props)}if(value){try{var primaryKey=eval("value['"+props.keyPath+"']");if(!primaryKey){if(props.autoInc==="true"){getNextAutoIncKey()}else{idbModules.util.throwDOMException(0,"Data Error - Could not eval key from keyPath")}}else{callback(primaryKey)}}catch(e){idbModules.util.throwDOMException(0,"Data Error - Could not eval key from keyPath",e)}}else{idbModules.util.throwDOMException(0,"Data Error - KeyPath was specified, but value was not")}}else{if(typeof key!=="undefined"){callback(key)}else{if(props.autoInc==="false"){idbModules.util.throwDOMException(0,"Data Error - The object store uses out-of-line keys and has no key generator and the key parameter was not provided. ",props)}else{getNextAutoIncKey()}}}})};IDBObjectStore.prototype.__insertData=function(tx,value,primaryKey,success,error){var paramMap={};if(typeof primaryKey!=="undefined"){paramMap.key=idbModules.Key.encode(primaryKey)}var indexes=JSON.parse(this.__storeProps.indexList);for(var key in indexes){try{paramMap[indexes[key].columnName]=idbModules.Key.encode(eval("value['"+indexes[key].keyPath+"']"))}catch(e){error(e)}}var sqlStart=["INSERT INTO ",idbModules.util.quote(this.name),"("];var sqlEnd=[" VALUES ("];var sqlValues=[];for(key in paramMap){sqlStart.push(key+",");sqlEnd.push("?,");sqlValues.push(paramMap[key])}sqlStart.push("value )");sqlEnd.push("?)");sqlValues.push(idbModules.Sca.encode(value));var sql=sqlStart.join(" ")+sqlEnd.join(" ");idbModules.DEBUG&&console.log("SQL for adding",sql,sqlValues);tx.executeSql(sql,sqlValues,function(tx,data){success(primaryKey)},function(tx,err){error(err)})};IDBObjectStore.prototype.add=function(value,key){var me=this;return me.transaction.__addToTransactionQueue(function(tx,args,success,error){me.__deriveKey(tx,value,key,function(primaryKey){me.__insertData(tx,value,primaryKey,success,error)})})};IDBObjectStore.prototype.put=function(value,key){var me=this;return me.transaction.__addToTransactionQueue(function(tx,args,success,error){me.__deriveKey(tx,value,key,function(primaryKey){var sql="DELETE FROM "+idbModules.util.quote(me.name)+" where key = ?";tx.executeSql(sql,[idbModules.Key.encode(primaryKey)],function(tx,data){idbModules.DEBUG&&console.log("Did the row with the",primaryKey,"exist? ",data.rowsAffected);me.__insertData(tx,value,primaryKey,success,error)},function(tx,err){error(err)})})})};IDBObjectStore.prototype.get=function(key){var me=this;return me.transaction.__addToTransactionQueue(function(tx,args,success,error){me.__waitForReady(function(){var primaryKey=idbModules.Key.encode(key);idbModules.DEBUG&&console.log("Fetching",me.name,primaryKey);tx.executeSql("SELECT * FROM "+idbModules.util.quote(me.name)+" where key = ?",[primaryKey],function(tx,data){idbModules.DEBUG&&console.log("Fetched data",data);try{if(0===data.rows.length){return success()}success(idbModules.Sca.decode(data.rows.item(0).value))}catch(e){idbModules.DEBUG&&console.log(e);success(undefined)}},function(tx,err){error(err)})})})};IDBObjectStore.prototype["delete"]=function(key){var me=this;return me.transaction.__addToTransactionQueue(function(tx,args,success,error){me.__waitForReady(function(){var primaryKey=idbModules.Key.encode(key);idbModules.DEBUG&&console.log("Fetching",me.name,primaryKey);tx.executeSql("DELETE FROM "+idbModules.util.quote(me.name)+" where key = ?",[primaryKey],function(tx,data){idbModules.DEBUG&&console.log("Deleted from database",data.rowsAffected);success()},function(tx,err){error(err)})})})};IDBObjectStore.prototype.clear=function(){var me=this;return me.transaction.__addToTransactionQueue(function(tx,args,success,error){me.__waitForReady(function(){tx.executeSql("DELETE FROM "+idbModules.util.quote(me.name),[],function(tx,data){idbModules.DEBUG&&console.log("Cleared all records from database",data.rowsAffected);success()},function(tx,err){error(err)})})})};IDBObjectStore.prototype.count=function(key){var me=this;return me.transaction.__addToTransactionQueue(function(tx,args,success,error){me.__waitForReady(function(){var sql="SELECT * FROM "+idbModules.util.quote(me.name)+((typeof key!=="undefined")?" WHERE key = ?":"");var sqlValues=[];(typeof key!=="undefined")&&sqlValues.push(idbModules.Key.encode(key));tx.executeSql(sql,sqlValues,function(tx,data){success(data.rows.length)},function(tx,err){error(err)})})})};IDBObjectStore.prototype.openCursor=function(range,direction){var cursorRequest=new idbModules.IDBRequest();var cursor=new idbModules.IDBCursor(range,direction,this,cursorRequest,"key","value");return cursorRequest};IDBObjectStore.prototype.index=function(indexName){var index=new idbModules.IDBIndex(indexName,this);return index};IDBObjectStore.prototype.createIndex=function(indexName,keyPath,optionalParameters){var me=this;optionalParameters=optionalParameters||{};me.__setReadyState("createIndex",false);var result=new idbModules.IDBIndex(indexName,me);me.__waitForReady(function(){result.__createIndex(indexName,keyPath,optionalParameters)},"createObjectStore");me.indexNames.push(indexName);return result};IDBObjectStore.prototype.deleteIndex=function(indexName){var result=new idbModules.IDBIndex(indexName,this,false);result.__deleteIndex(indexName);return result};idbModules.IDBObjectStore=IDBObjectStore}(idbModules));(function(c){var b=0;var a=1;var e=2;var d=function(f,k,g){if(typeof k==="number"){this.mode=k;(k!==2)&&c.DEBUG&&console.log("Mode should be a string, but was specified as ",k)}else{if(typeof k==="string"){switch(k){case"readwrite":this.mode=a;break;case"readonly":this.mode=b;break;default:this.mode=b;break}}}this.storeNames=typeof f==="string"?[f]:f;for(var h=0;h<this.storeNames.length;h++){if(!g.objectStoreNames.contains(this.storeNames[h])){c.util.throwDOMException(0,"The operation failed because the requested database object could not be found. For example, an object store did not exist but was being opened.",this.storeNames[h])}}this.__active=true;this.__running=false;this.__requests=[];this.__aborted=false;this.db=g;this.error=null;this.onabort=this.onerror=this.oncomplete=null;var j=this};d.prototype.__executeRequests=function(){if(this.__running&&this.mode!==e){c.DEBUG&&console.log("Looks like the request set is already running",this.mode);return}this.__running=true;var f=this;window.setTimeout(function(){if(f.mode!==2&&!f.__active){c.util.throwDOMException(0,"A request was placed against a transaction which is currently not active, or which is finished",f.__active)}f.db.__db.transaction(function(h){f.__tx=h;var l=null,k=0;function n(i,o){if(o){l.req=o}l.req.readyState="done";l.req.result=i;delete l.req.error;var p=c.Event("success");c.util.callback("onsuccess",l.req,p);k++;g()}function j(i){l.req.readyState="done";l.req.error="DOMError";var o=c.Event("error",arguments);c.util.callback("onerror",l.req,o);k++;g()}try{function g(){if(k>=f.__requests.length){f.__active=false;f.__requests=[];return}l=f.__requests[k];l.op(h,l.args,n,j)}g()}catch(m){c.DEBUG&&console.log("An exception occured in transaction",arguments);typeof f.onerror==="function"&&f.onerror()}},function(){c.DEBUG&&console.log("An error in transaction",arguments);typeof f.onerror==="function"&&f.onerror()},function(){c.DEBUG&&console.log("Transaction completed",arguments);typeof f.oncomplete==="function"&&f.oncomplete()})},1)};d.prototype.__addToTransactionQueue=function(h,f){if(!this.__active&&this.mode!==e){c.util.throwDOMException(0,"A request was placed against a transaction which is currently not active, or which is finished.",this.__mode)}var g=new c.IDBRequest();g.source=this.db;this.__requests.push({"op":h,"args":f,"req":g});this.__executeRequests();return g};d.prototype.objectStore=function(f){return new c.IDBObjectStore(f,this)};d.prototype.abort=function(){!this.__active&&c.util.throwDOMException(0,"A request was placed against a transaction which is currently not active, or which is finished",this.__active)};d.prototype.READ_ONLY=0;d.prototype.READ_WRITE=1;d.prototype.VERSION_CHANGE=2;c.IDBTransaction=d}(idbModules));(function(a){var b=function(d,e,c,g){this.__db=d;this.version=c;this.__storeProperties=g;this.objectStoreNames=new a.util.StringList();for(var f=0;f<g.rows.length;f++){this.objectStoreNames.push(g.rows.item(f).name)}this.name=e;this.onabort=this.onerror=this.onversionchange=null};b.prototype.createObjectStore=function(d,e){var f=this;e=e||{};e.keyPath=e.keyPath||null;var c=new a.IDBObjectStore(d,f.__versionTransaction,false);var g=f.__versionTransaction;g.__addToTransactionQueue(function(h,k,m,j){function i(){a.util.throwDOMException(0,"Could not create new object store",arguments)}if(!f.__versionTransaction){a.util.throwDOMException(0,"Invalid State error",f.transaction)}var l=["CREATE TABLE",a.util.quote(d),"(key BLOB",e.autoIncrement?", inc INTEGER PRIMARY KEY AUTOINCREMENT":"PRIMARY KEY",", value BLOB)"].join(" ");a.DEBUG&&console.log(l);h.executeSql(l,[],function(n,o){n.executeSql("INSERT INTO __sys__ VALUES (?,?,?,?)",[d,e.keyPath,e.autoIncrement?true:false,"{}"],function(){c.__setReadyState("createObjectStore",true);m(c)},i)},i)});f.objectStoreNames.push(d);return c};b.prototype.deleteObjectStore=function(d){var c=function(){a.util.throwDOMException(0,"Could not delete ObjectStore",arguments)};var e=this;!e.objectStoreNames.contains(d)&&c("Object Store does not exist");e.objectStoreNames.splice(e.objectStoreNames.indexOf(d),1);var f=e.__versionTransaction;f.__addToTransactionQueue(function(g,i,j,h){if(!e.__versionTransaction){a.util.throwDOMException(0,"Invalid State error",e.transaction)}e.__db.transaction(function(k){k.executeSql("SELECT * FROM __sys__ where name = ?",[d],function(l,m){if(m.rows.length>0){l.executeSql("DROP TABLE "+a.util.quote(d),[],function(){l.executeSql("DELETE FROM __sys__ WHERE name = ?",[d],function(){},c)},c)}})})})};b.prototype.close=function(){};b.prototype.transaction=function(c,e){var d=new a.IDBTransaction(c,e||1,this);return d};a.IDBDatabase=b}(idbModules));(function(d){var b=4*1024*1024;if(!window.openDatabase){return}var a=window.openDatabase("__sysdb__",1,"System Database",b);a.transaction(function(e){e.executeSql("SELECT * FROM dbVersions",[],function(f,g){},function(){a.transaction(function(f){f.executeSql("CREATE TABLE IF NOT EXISTS dbVersions (name VARCHAR(255), version INT);",[],function(){},function(){d.util.throwDOMException("Could not create table __sysdb__ to save DB versions")})})})},function(){d.DEBUG&&console.log("Error in sysdb transaction - when selecting from dbVersions",arguments)});var c={open:function(f,e){var i=new d.IDBOpenRequest();var g=false;function h(){if(g){return}var k=d.Event("error",arguments);i.readyState="done";i.error="DOMError";d.util.callback("onerror",i,k);g=true}function j(k){var l=window.openDatabase(f,1,f,b);i.readyState="done";if(typeof e==="undefined"){e=k||1}if(e<=0||k>e){d.util.throwDOMException(0,"An attempt was made to open a database using a lower version than the existing version.",e)}l.transaction(function(m){m.executeSql("CREATE TABLE IF NOT EXISTS __sys__ (name VARCHAR(255), keyPath VARCHAR(255), autoInc BOOLEAN, indexList BLOB)",[],function(){m.executeSql("SELECT * FROM __sys__",[],function(n,o){var p=d.Event("success");i.source=i.result=new d.IDBDatabase(l,f,e,o);if(k<e){a.transaction(function(q){q.executeSql("UPDATE dbVersions set version = ? where name = ?",[e,f],function(){var r=d.Event("upgradeneeded");r.oldVersion=k;r.newVersion=e;i.transaction=i.result.__versionTransaction=new d.IDBTransaction([],2,i.source);d.util.callback("onupgradeneeded",i,r,function(){var s=d.Event("success");d.util.callback("onsuccess",i,s)})},h)},h)}else{d.util.callback("onsuccess",i,p)}},h)},h)},h)}a.transaction(function(k){k.executeSql("SELECT * FROM dbVersions where name = ?",[f],function(l,m){if(m.rows.length===0){l.executeSql("INSERT INTO dbVersions VALUES (?,?)",[f,e||1],function(){j(0)},h)}else{j(m.rows.item(0).version)}},h)},h);return i},"deleteDatabase":function(h){var i=new d.IDBOpenRequest();var g=false;function j(l){if(g){return}i.readyState="done";i.error="DOMError";var k=d.Event("error");k.message=l;k.debug=arguments;d.util.callback("onerror",i,k);g=true}var e=null;function f(){a.transaction(function(k){k.executeSql("DELETE FROM dbVersions where name = ? ",[h],function(){i.result=undefined;var l=d.Event("success");l.newVersion=null;l.oldVersion=e;d.util.callback("onsuccess",i,l)},j)},j)}a.transaction(function(k){k.executeSql("SELECT * FROM dbVersions where name = ?",[h],function(l,n){if(n.rows.length===0){i.result=undefined;var o=d.Event("success");o.newVersion=null;o.oldVersion=e;d.util.callback("onsuccess",i,o);return}e=n.rows.item(0).version;var m=window.openDatabase(h,1,h,b);m.transaction(function(p){p.executeSql("SELECT * FROM __sys__",[],function(q,s){var r=s.rows;(function t(u){if(u>=r.length){q.executeSql("DROP TABLE __sys__",[],function(){f()},j)}else{q.executeSql("DROP TABLE "+d.util.quote(r.item(u).name),[],function(){t(u+1)},function(){t(u+1)})}}(0))},function(q){f()})},j)})},j);return i},"cmp":function(f,e){return d.Key.encode(f)>d.Key.encode(e)?1:f===e?0:-1}};d.shimIndexedDB=c}(idbModules));(function(b,a){if(typeof b.openDatabase!=="undefined"){b.shimIndexedDB=a.shimIndexedDB;if(b.shimIndexedDB){b.shimIndexedDB.__useShim=function(){b.indexedDB=a.shimIndexedDB;b.IDBDatabase=a.IDBDatabase;b.IDBTransaction=a.IDBTransaction;b.IDBCursor=a.IDBCursor;b.IDBKeyRange=a.IDBKeyRange};b.shimIndexedDB.__debug=function(c){a.DEBUG=c}}}b.indexedDB=b.indexedDB||b.webkitIndexedDB||b.mozIndexedDB||b.oIndexedDB||b.msIndexedDB;if(typeof b.indexedDB==="undefined"&&typeof b.openDatabase!=="undefined"){b.shimIndexedDB.__useShim()}else{b.IDBDatabase=b.IDBDatabase||b.webkitIDBDatabase;b.IDBTransaction=b.IDBTransaction||b.webkitIDBTransaction;b.IDBCursor=b.IDBCursor||b.webkitIDBCursor;b.IDBKeyRange=b.IDBKeyRange||b.webkitIDBKeyRange;if(!b.IDBTransaction){b.IDBTransaction={}}b.IDBTransaction.READ_ONLY=b.IDBTransaction.READ_ONLY||"readonly";b.IDBTransaction.READ_WRITE=b.IDBTransaction.READ_WRITE||"readwrite"}}(window,idbModules));