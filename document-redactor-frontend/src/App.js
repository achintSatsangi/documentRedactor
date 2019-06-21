import React, { Component } from "react";
import "./App.css";
import NavBar from "./components/navbar";
import Uploader from "./components/uploader";
import FileObject from "./components/fileObject";
import Output from "./components/output";
import LoadingSpinner from "./components/loadingSpinner";
import axios from "axios";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedFile: null,
      fileText: null,
      loading: false
    };
  }

  onClickHandler = () => {
    const data = new FormData();
    data.append("file", this.state.selectedFile);
    this.setState({ loading: true }, () => {
      axios
        .post("/upload", data, {
          // receive two    parameter endpoint url ,form data
        })
        .then(res => {
          const file = this.state.selectedFile;
          const text = res.data.textInFile;
          this.setState({
            selectedFile: file,
            fileText: text,
            loading: false
          });
        })
        .catch(error => {
          this.setState({ loading: false });
          // Error
          console.log(error);
          console.log(error.config);
          alert("Error: " + error.message);
        });
    });
  };

  onChangeHandler = event => {
    console.log(event.target.files);
    this.setState({
      selectedFile: event.target.files[0],
      fileText: null,
      loading: false
    });
  };

  isValidFileSelected() {
    return this.state.selectedFile !== null
      ? this.state.selectedFile.type.startsWith("image")
      : false;
  }

  render() {
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
              <br />
              <FileObject obj={this.state.selectedFile} />
            </div>
          </div>
          {this.state.loading ? (
            <LoadingSpinner />
          ) : (
            <Output text={this.state.fileText} />
          )}
        </div>
      </React.Fragment>
    );
  }
}

export default App;
