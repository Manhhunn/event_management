import { HomeOutlined, UserOutlined } from "@ant-design/icons";
import type { MenuProps } from "antd";
import { Menu } from "antd";
import { Link, useLocation } from "react-router";

type MenuItem = Required<MenuProps>["items"][number];

const items: MenuItem[] = [
  {
    label: <Link to="/">Home</Link>,
    key: "/",
    icon: <HomeOutlined />,
  },
  {
    label: <Link to="/users">User</Link>,
    key: "/users",
    icon: <UserOutlined />,
  },
];

const AppHeader = () => {
  const location = useLocation();
  return (
    <Menu
      selectedKeys={[location.pathname]}
      mode="horizontal"
      items={items}
    />
  );
};

export default AppHeader;
