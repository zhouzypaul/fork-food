import TopBar from "./TopBar";

/**
 * Renders about page.
 */
function About() {
  return (
    <>
      <TopBar to="/" showOptions={false}/>
      <div className="text">
        <h1 className="header1">what is fork?</h1>
        <p>
          Fork helps you make decisions in those fork-in-the-road moments. Don't know where to eat? Fork does. Simply go with a group or go solo. Fork will recommend nearby
          restaurants based on the group's preferences and within your budget. Our AI-backed algo is adaptive, so watch as it learns more
          about your food preferences and recommend better over time. All you have to do is swipe and fork will pick the group's favorite restaurant.
          No more indecisiveness just more eating and enjoying.
        </p>
        <h1>the team</h1>
        <ul>
          <li>Edward Xing</li>
          <li>Alan Gu</li>
          <li>Paul Zhou</li>
          <li>Sean Zhan</li>
        </ul>
      </div>
    </>
  );
}

export default About;