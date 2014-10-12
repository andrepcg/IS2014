<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">

<xsl:template match="/">

  <html lang="en">
  <head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Bootstrap 101 Template</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="estilo.css"/>

    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
    <div class="navbar navbar-default navbar-static-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Project name</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
          </ul>

        </div><!--/.nav-collapse -->
      </div>
    </div>

  
  <div class="container" id="noticias">

    <div class="row">
      <div class="col-sm-12">

      	<div class="btn-group" data-toggle="buttons">
                  <label class="btn btn-primary">
                    <input type="checkbox" name="options" id="us" data-section="U.S."> U.S.
                  </label>
                  <label class="btn btn-primary">
                    <input type="checkbox" name="options" id="africa" data-section="Africa"> Africa
                  </label>
                  <label class="btn btn-primary">
                    <input type="checkbox" name="options" id="africa" data-section="Asia"> Africa
                  </label>
                  <label class="btn btn-primary">
                    <input type="checkbox" name="options" id="africa" data-section="Europe"> Africa
                  </label>
                  <label class="btn btn-primary">
                    <input type="checkbox" name="options" id="africa" data-section="Latin America"> Africa
                  </label>
                  <label class="btn btn-primary">
                    <input type="checkbox" name="options" id="africa" data-section="Middle East"> Africa
                  </label>
                </div>

        <xsl:for-each select="news_list/article">
        
          <div class="noticia categoria_ciencia">
          	<xsl:attribute name="data-section"><xsl:value-of select="section"/></xsl:attribute>
            <div class="row">
              <div class="col-sm-3">
                <!-- style="background-image: url('http://placehold.it/202x176&text=Sem%20imagemdeshi')" -->
                <div class="img-rounded center-cropped">
                  <xsl:attribute name="style">
                      <xsl:value-of select="concat('background-image: url(',image,')')"/>                    
                  </xsl:attribute>
                </div>
              </div>
              <div class="col-sm-9">
                <h4 class="titulo"><xsl:value-of select="title"/></h4>
                <div class="meta">
                  <ul class="list-inline">
                    <li>
                      <span class="glyphicon glyphicon-book"></span>
                      <span class="jornal">
                        <xsl:value-of select="timestamp"/>
                      </span>
                    </li>
                    <li ><span class="glyphicon glyphicon-tag"></span> <span class="categoria"><xsl:attribute name="data-cat"><xsl:value-of select="section"/></xsl:attribute><xsl:value-of select="section"/></span></li>
                    <li ><span class="glyphicon glyphicon-globe"></span> <span class="url"><a><xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute>Link</a></span></li>
                  </ul>
                </div>
                  <blockquote>
                    <p class="texto"><xsl:value-of select="substring(corpus, 0, 400)"/></p>
                  </blockquote>
              </div>
            </div>
          </div>

        </xsl:for-each>

      </div>

    </div>


  </div>


    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
  
  <script>

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
    
    
  </script>
  </body>
</html>
</xsl:template>

</xsl:stylesheet>