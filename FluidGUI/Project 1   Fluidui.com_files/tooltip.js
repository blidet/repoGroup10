fluid.view.define("ui.tooltip",function(){var b,d,c;var e=false,a=false;this.create=function(f){f=f||document.body;if(f instanceof jQuery){f=f.get(0)}b=document.createElement("div");b.classList.add("arrow_box");d=document.createElement("span");d.classList.add("message");b.appendChild(d);f.appendChild(b);c=$(b)};this.show=function(g,f,h){d.textContent=g;f=f-(c.width()/2)+1;h=h-c.height()*3;if(e){c.stop(false,true)}a=true;c.css({"left":f+"px","top":h+"px"}).fadeIn({complete:function(){a=false}})};this.hide=function(){if(a){c.stop(false,true)}e=true;c.fadeOut({complete:function(){e=false}})}});