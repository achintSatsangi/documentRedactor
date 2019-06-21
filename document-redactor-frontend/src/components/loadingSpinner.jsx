import React from "react";
import loader from "../ajax-loader.gif";

const LoadingSpinner = () => (
  <div className="center w-100">
    <img src={loader} alt="Loading..." className="center w-25" />
  </div>
);

export default LoadingSpinner;
