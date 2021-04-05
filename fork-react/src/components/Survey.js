import { useState, useEffect } from "react";
import {Link} from "react-router-dom";
import TopBar from "./TopBar";

function Option(props) {
  return(<button className="survey-box">{props.value}</button>)
}

function Survey() {
  const [choices, setChoices] = useState([]);

  useEffect(() => {
    let boxes = [];
    for (let i = 0; i < 100; i++) {
      boxes.push(<Option value={i}/>);
    }
    setChoices(boxes);
  }, []);

  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="content">
        <div className="title-text">pick your favorites</div>
        <div className="survey">
          {choices}
        </div>
      </div>
      <div className="exit-home">
        <Link to="/home">
          <button className="secondary-button">
            skip
          </button>
        </Link>
      </div>
    </>
  );
}

export default Survey;