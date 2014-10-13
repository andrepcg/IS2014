  $( document ).ready(function() {

          $("input").change(function(){
            var l = [];
            $("input:checked").each(function(){
              l.push($(this).attr("data-section"));
            });

            if(l.length === 0){
              setVisible(["U.S.", "Africa", "Middle East", "Europe", "Asia", "Latin America"], true);
            }
            else{

              setVisible(l.length > 1 ? l : l[0], true);

              var l2 = [];
              $("input:not(:checked)").each(function(){
                l2.push($(this).attr("data-section"));
              });
              setVisible(l2.length > 1 ? l2 : l2[0], false);
            }
          });

          var setVisible = function(section, visible){
            if(typeof section === "string"){
              $(".noticia .categoria[data-cat='"+section+"']").each(function() {
                if(visible)
                  $( this ).closest(".noticia").removeClass("hidden");
                else
                  $( this ).closest(".noticia").addClass("hidden");
              });
            }
            else if(typeof section === "object" && section.length > 0){
              var f = ".noticia .categoria[data-cat='XXX']";
              var q = "";
              section.forEach(function(sec){
                  q += f.replace("XXX", sec) + ", ";
              });

              q = q.substr(0, q.length - 2);

              $(q).each(function() {
                if(visible)
                  $( this ).closest(".noticia").removeClass("hidden");
                else
                  $( this ).closest(".noticia").addClass("hidden");
              });
            }
          };


        });
    