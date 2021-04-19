import {useState, useEffect, useRef} from "react";
import {Link} from "react-router-dom";
import TopBar from "./TopBar";
import {useSelector} from "react-redux";
import axios from 'axios';

const TYPES = ["coffee & tea", "chinese", "pizza", "italian", "japanese", "indian", "greek",
  "middle eastern", "pizza", "vegan", "mexican", "thai", "american", "salad", "barbeque", "seafood",
  "steak", "vietnamese", "breakfast", "dessert"];
TYPES.sort();
const PRICES = ["$", "$$", "$$$"];
const CONFIG = {
  headers: {
    "Content-Type": "application/json",
    'Access-Control-Allow-Origin': '*',
  }
};

const SERVER_URL = 'http://localhost:4567';

/**
 * Renders survey option buttons.
 */
function Option(props) {
  const selected = useRef(props.selected);
  const [color, setColor] = useState("");

  // toggles color on click
  const toggle = (e) => {
    props.pick(e.target.innerText);
    selected.current = !selected.current;
    if (selected.current) {
      setColor("#DDAFF980");
    } else {
      setColor("gainsboro");
    }
  }

  // changes color based on selection
  useEffect(() => {
    setColor(props.selected ? "#DDAFF980" : "gainsboro");
  }, [props.selected]);

  return(
    <button className="survey-box" onClick={toggle} style={{backgroundColor: color}}>
      {props.value}
    </button>
  );
}

/**
 * Renders survey.
 */
function Survey(props) {
  const [types, setTypes] = useState([]);
  const [prices, setPrices] = useState([]);
  const [radius, setRadius] = useState(2);
  const [unit, setUnit] = useState("miles");
  const selectedTypes = useRef({});
  const priceRange = useRef({});
  const user = useSelector(state => state.user);
  const key = useRef(0);

  // generates option objects
  const generateObjects = (options, ref) => {
    let selections = {};
    options.forEach(op => {selections[op] = false});
    ref.current = selections;
  }

  // generates option boxes
  const generateBoxes = (options, handler, setter) => {
    const boxes = [];
    for (let op in options) {
      if (options.hasOwnProperty(op)) {
        boxes.push(<Option value={op} selected={options[op]} pick={handler} key={key.current++}/>);
      }
    }
    setter(boxes);
  }

  // gets user preferences to display
  const getPreferences = () => {
    const toSend = {
      username: user
    }

    axios.post(`${SERVER_URL}/getUserPref`, toSend, CONFIG)
      .then(response => {
        const data = response.data;
        if (data['err'].length !== 0) {
          alert(data['err']);
          return;
        }
        const types = data['types'];
        const prices = data['prices'];
        setRadius(data['radius']);
        types.forEach(t => selectType(t));
        prices.forEach(p => selectPrice(p));
        generateBoxes(selectedTypes.current, selectType, setTypes);
        generateBoxes(priceRange.current, selectPrice, setPrices);
      })

      .catch(function (e) {
        console.log(e);
      })
  }

  // changes value based on selection
  const selectType = (name) => {
    if (selectedTypes.current.hasOwnProperty(name)) {
      selectedTypes.current[name] = !selectedTypes.current[name];
    }
  }

  // changes value based on selection
  const selectPrice = (name) => {
    if (priceRange.current.hasOwnProperty(name)) {
      priceRange.current[name] = !priceRange.current[name];
    }
  }

  // generates survey on load
  useEffect(() => {
    generateObjects(TYPES, selectedTypes);
    generateObjects(PRICES, priceRange);
    getPreferences();
  }, []);

  // changes unit based on range
  const changeRange = (e) => {
    setRadius(e.target.value);
    if (e.target.value === "1") {
      setUnit("mile");
    } else {
      setUnit("miles");
    }
  }

  // saves preferences on back end
  const savePreferences = () => {
    // send to backend
    const typePref = [];
    const pricePref = [];
    const allTypes = selectedTypes.current;
    const allPrices = priceRange.current;
    for (let type in allTypes) {
      if (allTypes[type]) {
        typePref.push(type);
      }
    }
    for (let price in allPrices) {
      if (allPrices[price]){
        pricePref.push(price);
      }
    }
    const toSend = {
      username: user,
      types: typePref,
      price: pricePref,
      radius: radius
    }

    console.log(toSend)

    axios.post(`${SERVER_URL}/updateUserPref`, toSend, CONFIG)
      .then(response => {
        const data = response.data;
        if (!data['success']) {
          alert(data['err']);
        } else {
          props.history.push('/home');
        }
      })

      .catch(function (e) {
        console.log(e);
      })
  }

  // sets all option values to false
  const falsify = (options, setter) => {
    console.log("clearing")
    for (let op in options) {
      if (options.hasOwnProperty(op)) {
        options[op] = false;
      }
    }
    setter([]);
  }

  // clears selections
  const clear = () => {
    falsify(selectedTypes.current, setTypes);
    falsify(priceRange.current, setPrices);
    generateBoxes(selectedTypes.current, selectType, setTypes);
    generateBoxes(priceRange.current, selectPrice, setPrices);
  }

  return (
    <>
      <TopBar to="/home" showOptions={false} style={{backgroundColor: "#ffffff90"}}/>
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
        <div className="location">
          <button className="secondary-button" onClick={clear}>clear selections</button>
          <button className="primary-button" onClick={savePreferences}>save</button>
        </div>
      </div>
      <div className="about">
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
