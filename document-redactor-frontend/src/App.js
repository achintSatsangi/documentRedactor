import React, { Component } from "react";
import "./App.css";
import NavBar from "./components/navbar";
import Uploader from "./components/uploader";
import FileObject from "./components/fileObject";
import Output from "./components/output";
import LoadingSpinner from "./components/loadingSpinner";
import axios from "axios";
import FileSaver from "file-saver";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedFile: null,
      fileText: null,
      loading: false,
      resultHeader: null
    };
  }

  onClickExtractTextHandler = () => {
    this.getResponseFromServer("/extractText", "File contents");
  };

  onClickExtractPersonNumberHandler = () => {
    this.getResponseFromServer("/extractPersonNumber", "Person numbers");
  };

  onClickRedactPersonNumberHandler = () => {
    this.getFileFromServer(
      "/redactPersonNumber",
      "File will be downloaded to your downloads folder"
    );
  };

  getFileFromServer = (path, header) => {
    const data = new FormData();
    data.append("file", this.state.selectedFile);
    this.setState({ loading: true }, () => {
      axios
        .post(path, data, { responseType: "blob" })
        .then(res => {
          const file = this.state.selectedFile;
          this.setState({
            selectedFile: file,
            loading: false,
            fileText: "",
            resultHeader: header
          });
          const downloadFile = new Blob([res.data], { type: "image/PNG" });
          console.log(downloadFile.size); // !!! this line
          FileSaver.saveAs(downloadFile, "redactedImage.PNG");
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

  getResponseFromServer = (path, header) => {
    const data = new FormData();
    data.append("file", this.state.selectedFile);
    this.setState({ loading: true }, () => {
      axios
        .post(path, data, {
          // receive two    parameter endpoint url ,form data
        })
        .then(res => {
          const file = this.state.selectedFile;
          const text = res.data.textInFile;
          this.setState({
            selectedFile: file,
            fileText: text,
            loading: false,
            resultHeader: header
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
              <h5 className="p-2">
                This is a dummy app which uses image processing techniques to
                perform the following actions:
              </h5>
              <ul className="p-2">
                <li>Extract text - handwritten or printed</li>
                <li>Extract Norwwegian P numbers is various formats</li>
                <li>Redact P-numbers in the image and return the file</li>
              </ul>
              <h5 className="text-danger">
                Supports images in formats JPG, JPEG, PNG etc
              </h5>
            </div>
            <div className="col-md-6 center">
              <Uploader
                onFileChange={this.onChangeHandler}
                onClickExtractText={this.onClickExtractTextHandler}
                onClickExtractPersonNumber={
                  this.onClickExtractPersonNumberHandler
                }
                onClickRedactPersonNumber={
                  this.onClickRedactPersonNumberHandler
                }
                isUploadAllowed={this.isValidFileSelected()}
              />
              <br />
              <FileObject obj={this.state.selectedFile} />
            </div>
          </div>
          {this.state.loading ? (
            <LoadingSpinner />
          ) : (
            <Output
              text={this.state.fileText}
              header={this.state.resultHeader}
            />
          )}
        </div>
      </React.Fragment>
    );
  }
}

export default App;
