import React, { Component } from "react";

class Output extends Component {
  render() {
    if (this.props.text === null) return "";
    return (
      <div class="form-group green-border-focus">
        <label for="exampleFormControlTextarea5">{this.props.header}</label>
        <textarea
          class="form-control"
          id="exampleFormControlTextarea5"
          rows="3"
        >
          {this.props.text}
        </textarea>
      </div>
    );
  }
}

export default Output;
