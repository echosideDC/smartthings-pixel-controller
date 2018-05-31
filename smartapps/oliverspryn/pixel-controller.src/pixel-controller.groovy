metadata {
  definition(name: "PIXEL Controller", namespace: "oliverspryn", author: "Oliver Spryn") {
    capability "Switch"
  }

  tiles(scale: 2) {
    standardTile ("switch", "device.switch", width: 6, height: 4, canChangeIcon: true, decoration: "flat") {
      state("off", label: '${currentValue}', action: "switch.on",
            icon: "st.switches.switch.off", backgroundColor: "#ffffff")
      state("on", label: '${currentValue}', action: "switch.off",
            icon: "st.switches.switch.on", backgroundColor: "#00a0dc")
    }
    
    main("switch")
    details(["switch"])
  }
  
  preferences {
    input(name: "external", required: true, type: "enum", displayDuringSetup: true,
      options: [
        "Internal",
        "External"
      ],
      title: "Internal or External to your Network")
            
    input(name: "url", required: true, type: "text", displayDuringSetup: true,
      title: "URL (External) or IP Address (Internal)",
      description: "Enter the external URL or internal IP address to your PIXEL device")
            
    input(name: "image", required: true, type: "enum", displayDuringSetup: true,
      options: [
        "/still/monalista.png" : "Monalisa"
      ],
      title: "Image",
      description: "Image or animation to display when on")
  }
}

def parse(String description) {
  log.debug "Parsing '${description}'"
}

def on() {
  log.debug("Executing 'on'")
  urlRequest(image)
  sendEvent(name: "switch", value: "on")
}

def off() {
  log.debug("Executing 'off'")
  urlRequest("/still/zzzblank.png")
  sendEvent(name: "switch", value: "off")
}

private def urlRequest(String path) {
  def params = [
    uri: url,
    path: path
  ]
  
  if(external == "External") {
    log.debug("Making external call")
    
    try {
      httpGet(params) {
        log.debug("Finished external call")
      }
    } catch (e) {
      log.error("Could not make external call")
    }
      
    return
  }
  
  log.debug("Making internal call")
  
  def action = new physicalgraph.device.HubAction(
    method: "GET",
    path: params["path"],
    headers: [
      "Host": params["uri"]
    ]
  )
  
  sendHubCommand(action)
}
