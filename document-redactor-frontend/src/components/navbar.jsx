import React from "react";
import Logo from "../dr.png";

//Stateless functional component
function NavBar() {
  return (
    <nav className="navbar navbar-light bg-light">
      <a className="navbar-brand" href="#">
        <img src={Logo} height="50em" width="50em" />
        Document Redactor
      </a>
    </nav>
  );
}

export default NavBar;
