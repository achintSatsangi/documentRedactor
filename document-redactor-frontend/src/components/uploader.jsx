import React, { Component } from "react";

class Uploader extends Component {
  render() {
    return (
      <form method="post" action="#" id="#">
        <div className="form-group files">
          <input
            type="file"
            className="form-control"
            multiple={false}
            onChange={event => this.props.onFileChange(event)}
          />
        </div>
        <button
          type="button"
          className="btn btn-success btn-block"
          onClick={() => this.props.onClickUpload()}
          disabled={!this.props.isUploadAllowed}
        >
          Upload
        </button>
      </form>
    );
  }
}

export default Uploader;
