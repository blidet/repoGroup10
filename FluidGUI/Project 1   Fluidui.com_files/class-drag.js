window.requestAnimFrame=(function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||window.oRequestAnimationFrame||window.msRequestAnimationFrame||function(a){window.setTimeout(a,1000/60)}})();$(function(){document.addEventListener("mousedown",function(b){if(drag.draggable.length===0){return true}for(var a=0;a<drag.draggable.length;a++){if(drag.draggable[a].el===b.target){if(drag.draggable[a].beforeDrag&&!drag.draggable[a].beforeDrag(b)){return}break}else{if(a==drag.draggable.length-1){return}}}drag.startX=b.clientX;drag.startY=b.clientY;drag.lastX=b.clientX;drag.lastY=b.clientY;drag.lastTime=new Date().getTime();drag.mouseDown=true;drag.fn=drag.onMouseMove.bind(this,drag.draggable[a].el,drag.draggable[a]);window.addEventListener("mousemove",drag.fn);if(drag.draggable[a]&&drag.draggable[a].start){drag.draggable[a].start()}});document.addEventListener("mouseup",function(b){if(drag.mouseDown){if(drag.startX===b.clientX&&drag.startY===b.clientY){drag.clickOnly=true}else{drag.clickOnly=false}for(var a=0;a<drag.draggable.length;a++){if(drag.draggable[a].el===b.target||$(b.target).parents(drag.draggable[a].el).length){break}else{if(a==drag.draggable.length-1){drag.mouseDown=false;window.removeEventListener("mousemove",drag.fn);return true}}}}if(drag.draggable[a]&&drag.draggable[a].stop){drag.draggable[a].stop()}drag.mouseDown=false;window.removeEventListener("mousemove",drag.fn);return true});document.addEventListener("click",function(a){if(drag.clickOnly===false){a.stopImmediatePropagation()}drag.clickOnly=true});window.drag={evtInterval:17,draggable:[],onMouseMove:function(d,a,c){if(!drag.mouseDown){return}var b=new Date().getTime();if(b-drag.lastTime<drag.evtInterval){return}drag.lastTime=b;requestAnimFrame(function(){var f=drag.lastX-c.clientX;var e=drag.lastY-c.clientY;var g=d.parentElement.scrollTop;var h=d.parentElement.scrollLeft;d.parentElement.scrollTop=(g+e);d.parentElement.scrollLeft=(h+f);drag.lastX=c.clientX;drag.lastY=c.clientY;if(a.drag){a.drag()}})},makeDraggable:function(a){drag.draggable.push(a)},dragCheck:function(){}}});