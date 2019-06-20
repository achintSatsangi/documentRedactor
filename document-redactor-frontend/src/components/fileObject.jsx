import React, { Component } from "react";

class FileObject extends Component {
  state = {};
  render() {
    if (this.props.obj === null) return "";
    const file = this.props.obj;
    const displayWarning = file.type.startsWith("image");
    console.log(displayWarning);
    return (
      <div>
        File Details
        <ul>
          <li>Name : {file.name}</li>
          <li>Type : {file.type}</li>
        </ul>
        <p className="text-danger font-weight-bold" hidden={displayWarning}>
          File type should be image only
        </p>
      </div>
    );
  }
}

export default FileObject;
