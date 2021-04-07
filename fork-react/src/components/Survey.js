import {useState, useEffect, useRef} from "react";
import {Link} from "react-router-dom";
import TopBar from "./TopBar";

const TYPES = ["burgers", "chinese", "pizza", "italian", "sushi", "indian",
  "vietnamese", "steak", "breakfast", "dessert"];
const PRICES = ["$", "$$", "$$$"];

function Option(props) {
  const selected = useRef(false);
  const toggle = (e) => {
    props.pick(e.target.innerText);
    selected.current = !selected.current;
    if (selected.current) {
      e.target.style.backgroundColor = "#DDAFF980";
    } else {
      e.target.style.backgroundColor = "gainsboro";
    }
  }
  return(
    <button className="survey-box" onClick={toggle}>
      {props.value}
    </button>
  );
}

function Survey() {
  const [types, setTypes] = useState([]);
  const [prices, setPrices] = useState([]);
  const [radius, setRadius] = useState(2);
  const [unit, setUnit] = useState("miles");
  const selectedTypes = useRef({});
  const priceRange = useRef({});

  const generateBoxes = (options, handler, setter, ref) => {
    let boxes = [];
    let selections = {};
    let i = 1;
    for (let t of options) {
      boxes.push(<Option value={t} pick={handler} key={i}/>);
      selections[t] = false;
      i++;
    }
    ref.current = selections;
    setter(boxes);
  }

  const changeSelect = (name) => {
    selectedTypes.current[name] = !selectedTypes.current[name];
  }

  const changePrice = (name) => {
    priceRange.current[name] = !priceRange.current[name];
  }

  useEffect(() => {
    generateBoxes(TYPES, changeSelect, setTypes, selectedTypes)
  }, []);

  useEffect(() => {
    generateBoxes(PRICES, changePrice, setPrices, priceRange);
  }, []);

  const changeRange = (e) => {
    setRadius(e.target.value);
    if (e.target.value === "1") {
      setUnit("mile");
    } else {
      setUnit("miles");
    }
  }

  const savePreferences = () => {
    // send to backend
    const typePref = [];
    const pricePref = [];
    const types = selectedTypes.current;
    const prices = priceRange.current;
    for (let type in types) {
      if (types[type]) {
        typePref.push(type);
      }
    }
    for (let price in prices) {
      if (prices[price]){
        pricePref.push(price);
      }
    }
    const toSend = {
      types: typePref,
      price: pricePref,
      radius: radius
    }
    console.log(toSend);
  }

  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="content">
        <div className="title-text">pick your favorites</div>
        <div className="survey">
          {types}
        </div>
        <div className="title-text">pick your price range</div>
        <div className="survey">
          {prices}
        </div>
        <div className="title-text">pick your range</div>
        <div className="slider-container">
          <input id="radius" type="range" min="1" max="50" value={radius} onChange={changeRange}/>
          <div id="radius-display">
            {radius} {unit}
          </div>
        </div>
        <button className="primary-button" onClick={savePreferences}>save</button>
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