private boolean generateHTML(String timestamp, String xmlFile) {
		
		String html = this.HTMLPage + "_" + timestamp + ".html"; 
		
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource xslt = new StreamSource(this.XSLTransformer);
		
		try {
			
			Transformer transformer = factory.newTransformer(xslt);
			StreamSource text = new StreamSource(xmlFile);
			transformer.transform(text, new StreamResult(html));
		
		} catch (TransformerConfigurationException e) {
			return false;
		} catch (TransformerException e) {
			return false;
		}
		return true;
	}