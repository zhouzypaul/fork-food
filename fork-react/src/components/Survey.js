import {useState, useEffect, useRef} from "react";
import {Link} from "react-router-dom";
import TopBar from "./TopBar";

const TYPES = ["burgers", "chinese", "pizza", "italian", "sushi", "indian",
  "vietnamese", "steak", "breakfast", "dessert"]

function Option(props) {
  const selected = useRef(false);
  const toggle = (e) => {
    props.pick(e.target.innerText);
    selected.current = !selected.current;
    if (selected.current) {
      e.target.style.backgroundColor = "#DDAFF980"
    } else {
      e.target.style.backgroundColor = "gainsboro";
    }
  }
  return(
    <button className="survey-box" onClick={toggle}>
      {props.value}
    </button>);
}

function Survey() {
  const [choices, setChoices] = useState([]);
  const selected = useRef({});

  const changeSelect = (name) => {
    selected.current[name] = !selected.current[name];
  }

  useEffect(() => {
    let boxes = [];
    let selections = {};
    let i = 1;
    for (let t of TYPES) {
      boxes.push(<Option value={t} pick={changeSelect} key={i}/>);
      selections[t] = false;
      i++;
    }
    selected.current = selections;
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