import React, { Component } from "react";
import "./App.css";
import NavBar from "./components/navbar";
import Uploader from "./components/uploader";
import FileObject from "./components/fileObject";
import axios from "axios";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedFile: null
    };
  }

  onClickHandler = () => {
    const data = new FormData();
    data.append("file", this.state.selectedFile);
    axios
      .post("/upload", data, {
        // receive two    parameter endpoint url ,form data
      })
      .then(res => {
        // then print response status
        console.log(res.statusText);
      });
  };

  onChangeHandler = event => {
    console.log(event.target.files);
    this.setState({
      selectedFile: event.target.files[0],
      loaded: 0
    });
  };

  isValidFileSelected() {
    return this.state.selectedFile !== null
      ? this.state.selectedFile.type.startsWith("image")
      : false;
  }

  render() {
    console.log(this.state.selectedFile);
    console.log(this.isValidFileSelected());
    return (
      <React.Fragment>
        <NavBar />
        <div className="container">
          <div className="row">
            <div className="col-md-6 center">
              <Uploader
                onFileChange={this.onChangeHandler}
                onClickUpload={this.onClickHandler}
                isUploadAllowed={this.isValidFileSelected()}
              />
              <FileObject obj={this.state.selectedFile} />
            </div>
          </div>
        </div>
      </React.Fragment>
    );
  }
}

export default App;
