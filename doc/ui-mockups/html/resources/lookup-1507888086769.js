(function(window, undefined) {
  var dictionary = {
    "4113f418-9e77-4517-b526-f4467b243da0": "Social Nearby Screen",
    "f4d21e35-895e-4440-9eb6-3afa82193c29": "Details Screen",
    "aa2170f6-010e-4781-bab3-c881a36785db": "Map Screen",
    "7a4b0458-6893-45ab-90e9-9bd932cdd08c": "Edit Habit Event Screen",
    "b88a3bc9-e49d-4fd2-9512-e30813722e6f": "Social Map Screen",
    "cadcdbd5-d639-4865-b840-0c142b724760": "Home screen",
    "6e651d3c-08ee-442b-9356-3ec121135cad": "Nearby Screen",
    "2ba16740-946a-47f6-ae91-8b78e230ad66": "Edit Type Screen",
    "4edb22d7-86b8-4439-8296-cdbb2a294ba8": "Permissions Screen",
    "8f949eca-ab19-49ac-b158-1618f1c818dc": "History Screen",
    "f831a43b-5754-4e2d-a7a2-ef5c29b93a11": "Permission Details Screen",
    "ecacbf47-3c45-4b64-aafd-a78ed5713f1b": "Add Habit Screen",
    "caf75b39-7d4e-4555-8e34-239f08687e44": "Social Screen",
    "12f95033-51c0-4a0b-bb64-c80c715ca252": "Error screen",
    "8a4897e9-b064-4cfa-b08e-6f61f2d8ba02": "Splash screen",
    "56984176-55e3-42f9-8f05-350b5f67c7db": "Filter Screen",
    "1432c19d-c891-4d09-9d9e-a0e9b91f9494": "login screen",
    "7d9a839c-862d-4d00-8ff2-94f18e019662": "Add Type Screen",
    "f39803f7-df02-4169-93eb-7547fb8c961a": "Template 1",
    "bb8abf58-f55e-472d-af05-a7d1bb0cc014": "default"
  };

  var uriRE = /^(\/#)?(screens|templates|masters|scenarios)\/(.*)(\.html)?/;
  window.lookUpURL = function(fragment) {
    var matches = uriRE.exec(fragment || "") || [],
        folder = matches[2] || "",
        canvas = matches[3] || "",
        name, url;
    if(dictionary.hasOwnProperty(canvas)) { /* search by name */
      url = folder + "/" + canvas;
    }
    return url;
  };

  window.lookUpName = function(fragment) {
    var matches = uriRE.exec(fragment || "") || [],
        folder = matches[2] || "",
        canvas = matches[3] || "",
        name, canvasName;
    if(dictionary.hasOwnProperty(canvas)) { /* search by name */
      canvasName = dictionary[canvas];
    }
    return canvasName;
  };
})(window);